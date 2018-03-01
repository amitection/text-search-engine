package com.ir.searchengine.result;


/** POJO for Result.
 * @author amit
 */
public class Result {

	private String a_queryNumber;
	
	private String b_q0;
	
	private String c_docId;
	
	private String d_rank;
	
	private String e_score;
	
	private String f_exp;

	public Result(String queryNumber, String q0, String docId, String rank, String score, String exp) {
		super();
		this.a_queryNumber = queryNumber;
		this.b_q0 = q0;
		this.c_docId = docId;
		this.d_rank = rank;
		this.e_score = score;
		this.f_exp = exp;
	}

	public String getA_queryNumber() {
		return a_queryNumber;
	}

	public void setA_queryNumber(String a_queryNumber) {
		this.a_queryNumber = a_queryNumber;
	}

	public String getB_q0() {
		return b_q0;
	}

	public void setB_q0(String b_q0) {
		this.b_q0 = b_q0;
	}

	public String getC_docId() {
		return c_docId;
	}

	public void setC_docId(String c_docId) {
		this.c_docId = c_docId;
	}

	public String getD_rank() {
		return d_rank;
	}

	public void setD_rank(String d_rank) {
		this.d_rank = d_rank;
	}

	public String getE_score() {
		return e_score;
	}

	public void setE_score(String e_score) {
		this.e_score = e_score;
	}

	public String getF_exp() {
		return f_exp;
	}

	public void setF_exp(String f_exp) {
		this.f_exp = f_exp;
	}
}
