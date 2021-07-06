package ac.il.afeka.fsm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StateSet extends IdentifiedState implements Iterable<State> {
	private Set<State> statesContained;

	public StateSet(Integer i) {
		super(i);
		statesContained = new HashSet<State>();
	}
	
	public StateSet(Integer i, Set<State> statesContained) {
		this(i);
		this.statesContained = statesContained;
	}
	
	public StateSet(Set<State> statesContained) {
		this(null, statesContained);
	}
	
	public StateSet() {
		this((Integer)null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statesContained == null) ? 0 : statesContained.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateSet other = (StateSet) obj;
		if (statesContained == null) {
			if (other.statesContained != null)
				return false;
		} else if (!statesContained.equals(other.statesContained))
			return false;
		return true;
	}

	public boolean addState(State s) {
		return statesContained.add(s);
	}
	
	public boolean addAll(Set<State> states) {
		return statesContained.addAll(states);
	}
	
	public boolean addStateSet(StateSet s) {
		return addAll(s.statesContained);
	}
	
	@Override
	public Iterator<State> iterator() {
		return statesContained.iterator();
	}
	
	@Override
	public String toString() {
		if (id == null) {
			StringBuilder sb = new StringBuilder("{");
			for (State s : this)
				sb.append(s.toString() + ", ");

			if (sb.toString().endsWith(", "))
				sb.replace(sb.length() - 2, sb.length(), "");

			sb.append("}");
			return sb.toString();
		}

		return super.toString();
	}

}
