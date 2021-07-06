package ac.il.afeka.fsm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class NDFSM {

	protected TransitionMapping transitions;
	protected Set<State> states;
	protected Set<State> acceptingStates;
	protected State initialState;
	protected Alphabet alphabet;

	/**
	 * Builds a NDFSM from a string representation (encoding)
	 * 
	 * @param encoding the string representation of a NDFSM
	 * @throws Exception if the encoding is incorrect or if the transitions contain
	 *                   invalid states or symbols
	 */
	public NDFSM(String encoding) throws Exception {
		parse(encoding);

		transitions.verify(states, alphabet);
	}

	/**
	 * Build a NDFSM from its components
	 * 
	 * @param states          the set of states for this machine
	 * @param alphabet        this machine's alphabet
	 * @param transitions     the transition mapping of this machine
	 * @param initialState    the initial state (must be a member of states)
	 * @param acceptingStates the set of accepting states (must be a subset of
	 *                        states)
	 * @throws Exception if the components do not represent a valid non
	 *                   deterministic machine
	 */
	public NDFSM(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState,
			Set<State> acceptingStates) throws Exception {

		initializeFrom(states, alphabet, transitions, initialState, acceptingStates);
		this.transitions.verify(this.states, alphabet);
	}

	protected void initializeFrom(Set<State> states, Alphabet alphabet, Set<Transition> transitions, State initialState,
			Set<State> acceptingStates) {

		this.states = states;
		this.alphabet = alphabet;
		this.transitions = createMapping(transitions);
		this.initialState = initialState;
		this.acceptingStates = acceptingStates;
	}

	protected NDFSM() {
	}

	/**
	 * Overrides this machine with the machine encoded in string.
	 * 
	 * <p>
	 * Here's an example of the encoding:
	 * </p>
	 * 
	 * <pre>
	0 1/a b/0 , a , 0; 0,b, 1 ;1, a, 0 ; 1, b, 1/0/ 1
	 * </pre>
	 * <p>
	 * This is the encoding of a finite state machine with two states (identified as
	 * 0 and 1), an alphabet that consists of the two characters 'a' and 'b', and
	 * four transitions:
	 * </p>
	 * <ol>
	 * <li>From state 0 on character a it moves to state 0</li>
	 * <li>from state 0 on character b it moves to state 1,</li>
	 * <li>from state 1 on character a it moves to state 0,</li>
	 * <li>from state 1 on character b it moves to state 1.</li>
	 * </ol>
	 * <p>
	 * The initial state of this machine is 0, and the set of accepting states
	 * consists of just one state 1. Here is the format in general:
	 * </p>
	 * 
	 * <pre>
	 {@code
	<states> / <alphabet> / <transitions> / <initial state> / <accepting states>
	}
	 * </pre>
	 * 
	 * where:
	 * 
	 * <pre>
	{@code
	<alphabet> is <char> <char> ...

	<transitions> is <transition> ; <transition> ...

	<transition> is from , char, to

	<initial state> is an integer

	<accepting states> is <state> <state> ...

	<state> is an integer
	}
	 * </pre>
	 * 
	 * @param string the string encoding
	 * @throws Exception if the string encoding is invalid
	 */
	public void parse(String string) throws Exception {

		Scanner scanner = new Scanner(string);	

		scanner.useDelimiter("\\s*/");

		Map<Integer, State> states = new HashMap<Integer, State>();

		for (Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
			states.put(stateId, new IdentifiedState(stateId));
		}

		Alphabet alphabet = Alphabet.parse(scanner.next());

		Set<Transition> transitions = new HashSet<Transition>();

		for (TransitionTuple t : TransitionTuple.parseTupleList(scanner.next())) {
			transitions.add(new Transition(states.get(t.fromStateId()), t.symbol(), states.get(t.toStateId())));
		}

		State initialState = states.get(scanner.nextInt());

		Set<State> acceptingStates = new HashSet<State>();

		if (scanner.hasNext())
			for (Integer stateId : IdentifiedState.parseStateIdList(scanner.next())) {
				acceptingStates.add(states.get(stateId));
			}

		scanner.close();

		initializeFrom(new HashSet<State>(states.values()), alphabet, transitions, initialState, acceptingStates);
		this.transitions.verify(this.states, alphabet);
	}

	protected TransitionMapping createMapping(Set<Transition> transitions) {
		return new TransitionRelation(transitions);
	}

	/**
	 * Returns a version of this state machine with all the unreachable states
	 * removed.
	 * 
	 * @return NDFSM that recognizes the same language as this machine, but has no
	 *         unreachable states.
	 */
	public NDFSM removeUnreachableStates() {

		Set<State> reachableStates = reachableStates();

		Set<Transition> transitionsToReachableStates = new HashSet<Transition>();

		for (Transition t : transitions.transitions()) {
			if (reachableStates.contains(t.fromState()) && reachableStates.contains(t.toState()))
				transitionsToReachableStates.add(t);
		}

		Set<State> reachableAcceptingStates = new HashSet<State>();
		for (State s : acceptingStates) {
			if (reachableStates.contains(s))
				reachableAcceptingStates.add(s);
		}

		NDFSM aNDFSM = (NDFSM) create();

		aNDFSM.initializeFrom(reachableStates, alphabet, transitionsToReachableStates, initialState,
				reachableAcceptingStates);

		return aNDFSM;
	}

	protected NDFSM create() {
		return new NDFSM();
	}

	// returns a set of all states that are reachable from the initial state

	private Set<State> reachableStates() {

		List<Character> symbols = new ArrayList<Character>();

		symbols.add(Alphabet.EPSILON);

		for (Character c : alphabet) {
			symbols.add(c);
		}

		Alphabet alphabetWithEpsilon = new Alphabet(symbols);

		Set<State> reachable = new HashSet<State>();

		Set<State> newlyReachable = new HashSet<State>();

		newlyReachable.add(initialState);

		while (!newlyReachable.isEmpty()) {
			reachable.addAll(newlyReachable);
			newlyReachable = new HashSet<State>();
			for (State state : reachable) {
				for (Character symbol : alphabetWithEpsilon) {
					for (State s : transitions.at(state, symbol)) {
						if (!reachable.contains(s))
							newlyReachable.add(s);
					}
				}
			}
		}

		return reachable;
	}

	/**
	 * Encodes this state machine as a string
	 * 
	 * @return the string encoding of this state machine
	 */
	public String encode() {
		return State.encodeStateSet(states) + "/" + alphabet.encode() + "/" + transitions.encode() + "/"
				+ initialState.encode() + "/" + State.encodeStateSet(acceptingStates);
	}

	/**
	 * Prints a set notation description of this machine.
	 * 
	 * <p>
	 * To see the Greek symbols on the console in Eclipse, go to Window -&gt;
	 * Preferences -&gt; General -&gt; Workspace and change
	 * <tt>Text file encoding</tt> to <tt>UTF-8</tt>.
	 * </p>
	 * 
	 * @param out the output stream on which the description is printed.
	 */
	public void prettyPrint(PrintStream out) {
		out.print("K = ");
		State.prettyPrintStateSet(states, out);
		out.println("");

		out.print("\u03A3 = ");
		alphabet.prettyPrint(out);
		out.println("");

		out.print(transitions.prettyName() + " = ");
		transitions.prettyPrint(out);
		out.println("");

		out.print("s = ");
		initialState.prettyPrint(out);
		out.println("");

		out.print("A = ");
		State.prettyPrintStateSet(acceptingStates, out);
		out.println("");
	}

	/**
	 * Returns a canonic version of this machine.
	 * 
	 * <p>
	 * The canonic encoding of two minimal state machines that recognize the same
	 * language is identical.
	 * </p>
	 * 
	 * @return a canonic version of this machine.
	 */

	public NDFSM toCanonicForm() {

		Set<Character> alphabetAndEpsilon = new HashSet<Character>();

		for (Character symbol : alphabet) {
			alphabetAndEpsilon.add(symbol);
		}
		alphabetAndEpsilon.add(Alphabet.EPSILON);

		Set<Transition> canonicTransitions = new HashSet<Transition>();
		Stack<State> todo = new Stack<State>();
		Map<State, State> canonicStates = new HashMap<State, State>();
		Integer free = 0;

		todo.push(initialState);
		canonicStates.put(initialState, new IdentifiedState(free));
		free++;

		while (!todo.isEmpty()) {
			State top = todo.pop();
			for (Character symbol : alphabetAndEpsilon) {
				for (State nextState : transitions.at(top, symbol)) {
					if (!canonicStates.containsKey(nextState)) {
						canonicStates.put(nextState, new IdentifiedState(free));
						todo.push(nextState);
						free++;
					}
					canonicTransitions
					.add(new Transition(canonicStates.get(top), symbol, canonicStates.get(nextState)));
				}
			}
		}

		Set<State> canonicAcceptingStates = new HashSet<State>();
		for (State s : acceptingStates) {
			if (canonicStates.containsKey(s)) // unreachable accepting states will not appear in the canonic form of the
				// state machine
				canonicAcceptingStates.add(canonicStates.get(s));
		}

		NDFSM aNDFSM = create();

		aNDFSM.initializeFrom(new HashSet<State>(canonicStates.values()), alphabet, canonicTransitions,
				canonicStates.get(initialState), canonicAcceptingStates);

		return aNDFSM;
	}

	public boolean compute(String input) throws Exception {
		return toDFSM().compute(input);
	}

	private boolean isStateSetAccept(StateSet s) {
		for (State state : s) {
			if(acceptingStates.contains(state))
				return true;
		}
		return false;
	}

	private StateSet stateSetTransition(StateSet state, Character ch) {
		StateSet toState = new StateSet();
		for (State s : state) {
			for (State next : transitions.at(s, ch)) 
				toState.addStateSet(epsClose(next));
		}
		return toState;
	}

	public DFSM toDFSM() throws Exception {
		Set<Transition> trans = new LinkedHashSet<Transition>();
		Set<State> states = new LinkedHashSet<State>();
		Set<State> acceptStates = new LinkedHashSet<State>();
		StateSet initState = epsClose(initialState);
		boolean hasDead = false;
		StateSet dead = new StateSet();
		Queue<StateSet> todo = new LinkedList<StateSet>();
		todo.add(initState);
		states.add(initState);
		if (isStateSetAccept(initState))
			acceptStates.add(initState);
		while (!todo.isEmpty()) {
			StateSet fromState = todo.remove();
			for (Character ch : alphabet) {
				StateSet toState = stateSetTransition(fromState, ch);
				if(toState.equals(dead)) {
					hasDead = true;
					trans.add(new Transition(fromState, ch, dead));
				} else {
					if (!states.contains(toState)) {
						todo.add(toState);
						states.add(toState);
						if (isStateSetAccept(toState))
							acceptStates.add(toState);
						trans.add(new Transition(fromState, ch, toState));
					} else {
						for (State s : states) {
							if (s.equals(toState))
								trans.add(new Transition(fromState, ch, s));
						}
					}
				}
			}
		}
		if (hasDead) {
			for (Character ch : alphabet) 
				trans.add(new Transition(dead, ch, dead));
			states.add(dead);
		}
		int count = 0;
		for (State s : states) 
			((StateSet) s).setId(count++);
		return new DFSM(states, alphabet, trans, initState, acceptStates);	
	}

	private Set<State> eps(State state) {
		Set<State> epsSet = transitions.at(state, Alphabet.EPSILON);
		if (epsSet.isEmpty()) 
			epsSet.add(state);
		else {
			for (State s : epsSet) {
				if (!s.equals(state))
					epsSet.addAll(eps(s));
			}
		}
		return epsSet;
	}

	private StateSet epsClose(State s) {
		StateSet close = new StateSet(eps(s));
		close.addState(s);
		return close;
	}
}
