import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ac.il.afeka.fsm.DFSM;
import ac.il.afeka.fsm.NDFSM;

public class TestToDFSM {

	@Test
	public void testBookExample() {
		String encoding = "1 2 3 4 5 6 7 8/a b c/1,b,1;1,,2;2,b,3;2,b,5;2,,7;3,a,4;3,c,4;4,c,2;4,c,7;5,a,6;5,b,6;6,c,2;6,c,7;6,,2;7,b,8/1/8";

		String equivalentEncoding = "0 1 2 3 4 5 6 7 8 9/a b c/" + "0,a,9;0,b,1;0,c,9;" + "1,a,2;1,b,3;1,c,4;"
				+ "2,a,9;2,b,5;2,c,6;" + "3,a,2;3,b,3;3,c,7;" + "4,a,9;4,b,9;4,c,6;" + "5,a,2;5,b,8;5,c,4;"
				+ "6,a,9;6,b,5;6,c,9;" + "7,a,9;7,b,5;7,c,6;" + "8,a,9;8,b,5;8,c,6;"
				+ "9,a,9;9,b,9;9,c,9/0/1 3 5";

		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNoEpsilonTransitions() {
		String encoding = "1 2 3 4/a b/1,a,1;1,b,1;1,a,2;2,a,3;2,b,3;3,a,4;3,b,4/1/4";
		String equivalentEncoding = "0 1 2 3 4 5 6 7/a b/3,b,7; 0,a,1;\n" + "7,a,1; 3,a,6; 7,b,0; 4,b,5; 2,b,5;\n"
				+ "1,b,3; 0,b,0; 1,a,2; 2,a,4; 4,a,4;\n" + "5,b,7; 5,a,6; 6,a,2; 6,b,3/0/4 5 6\n" + "7";

		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeterministic() {
		String encoding = "0 1/a/0,a,1;1,a,1/0/";

		String equivalentEncoding = "0 1/a/0,a,1;1,a,1/0/";

		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEmptyLanguage() {
		String encoding = "0/a/0,,0/0/";
		String equivalentEncoding = "0 1/a/0,a,1;1,a,1/0/";

		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOddLengthString() {
		String encoding = "1 2/0 1/1,0,1;1,1,2;2,0,2;2,1,1/1/2";
		String equivalentEncoding = "0 1/0 1/0,0,0;0,1,1;1,0,1;1,1,0/0/1";
		
		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAllStrings() {
		String encoding = "0/a/0,,0;0,a,0/0/0";
		String equivalentEncoding = "0/a/0,a,0/0/0";
		
		try {
			NDFSM ndfsm = new NDFSM(encoding);
			assertEquals(new DFSM(equivalentEncoding).encode(), ndfsm.toDFSM().encode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
