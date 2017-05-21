
/* ***************************************************************************
                         Seyedehzahra Hosseini 
                         Hamid Bagheri

*******************************************************************************/


public class DocSco {
public int docId;
public double score;
public int getDocId() {
	return docId;
}
public void setDocId(int docId) {
	this.docId = docId;
}
public double getScore() {
	return score;
}
public void setScore(int score) {
	this.score = score;
}
public DocSco(int docId, double score) {
	super();
	this.docId = docId;
	this.score = score;
}

}
