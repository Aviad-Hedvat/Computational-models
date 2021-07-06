import java.util.Arrays;
import java.util.List;
import ac.il.afeka.Submission.Submission;
import ac.il.afeka.fsm.NDFSM;

public class Main implements Submission, Assignment3 {

	@Override
	public List<String> submittingStudentIds() {
		return Arrays.asList("207115205", "207116112");
	}

	@Override
	public String convert(String ndfsm) throws Exception {
		NDFSM NDFSM = new NDFSM(ndfsm);
		return NDFSM.toDFSM().encode();
	}

}
