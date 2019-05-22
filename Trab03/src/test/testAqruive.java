package test;

//import mainpackage.Arquive;

public class testAqruive {

	public static void main(String[] args) {
	//	Arquive a = new Arquive("XXYYZZ11 teste01.docx user01@inf1416.puc-rio.br usuario");
		char[] passwd = System.console().readPassword("[%s]", "Password:");
		System.out.println(passwd);
	}

}
