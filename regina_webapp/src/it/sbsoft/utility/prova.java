package it.sbsoft.utility;

public class prova {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String mp = "100A;245;186,100B;245;217,101A;314;186,101B;314;217,102A;389;186,102B;389;217,103A;453;179,103B;453;207,104A;588;176,104B;588;206,105A;655;179,105B;655;210,106A;726;180,106B;726;210,107A;800;189,107B;800;212,108A;863;165,109A;925;144,109B;925;173,110A;1007;135,110B;1002;164,111A;670;346,111B;703;345,111C;819;357,111D;849;357";
		String[] sp = mp.split(",");
		
		for (int i=0; i<sp.length; i++) {
			System.out.println(sp[i]);
		}
	}

}
