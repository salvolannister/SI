package test;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Scanner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import mainpackage.Arquives;
import mainpackage.PasswordChecker;

public class test4 {

	public static void main(String[] args) {
		/* check password insertion */
		String password = PasswordChecker.readPassword("– Senha pessoal: ");
		//boolean ciao = printRegisterUser();
	}

	private static boolean printRegisterUser() {
		 Scanner scanner = new Scanner(System.in);
		 System.out.println("");
		 System.out.println("Formulário de Cadastro:");
		 System.out.println("");
		 System.out.print("– Caminho do arquivo do certificado digital<campo com 255 caracteres>: ");
		 String path = new String(scanner.nextLine());
		 while (path.length() > 255) {
			 System.out.println("caminho deve ser max 255 caaracteres, insert again:");
			 path = new String(scanner.nextLine());
		 }
		
		 System.out.print("\n– Grupo User=0 Admin=1 : ");
		 int gid = scanner.nextInt();
		 while(gid!= 0 && gid !=1) {
			 System.out.println("Grupo pode ser User=0 ou Admin=1 ");
			 System.out.print("\nEscreve de novo:  ");
			 gid = scanner.nextInt();
		 }
		 System.out.println("– Senha pessoal<seis, sete ou oito dígitos>: ");
		 
		 String password = new String(scanner.nextLine());
		 password = new String(scanner.nextLine());
		 boolean OK = checkPassword(password);
		  while(!OK ) {
			     System.out.print("\nEsreve de novo a password: ");
			     password = new String(scanner.nextLine());
			  	 OK = checkPassword(password);
				
		  }
		  System.out.println("Press 1 para concluir Press 0 para voltar no menu principal 1");
		  int n = scanner.nextInt();
		  if(n == 1) {
			  
			try {
				System.out.print("###################################################\n");
				  byte[] certificate = Arquives.ReadArquive(Paths.get(path));
				  String[] values;
				values = Arquives.readCertificate(certificate);
				  /* instead of receving a String values, the best way could be using a class*/
				 X509Certificate x509Certificate;
			
				x509Certificate = X509Certificate.getInstance(certificate);
				System.out.println("Version: "+x509Certificate.getVersion());
				 System.out.println("Serial number: "+x509Certificate.getSerialNumber().toString());
				 System.out.println("Certificate valid before: "+x509Certificate.getNotBefore().toString()+
						 			" Certificate valid after: "+ x509Certificate.getNotAfter().toString());
				 System.out.println("Signature algorithm: "+x509Certificate.getSigAlgName().toString());
				 System.out.println("Issuer info: "+ x509Certificate.getIssuerDN().getName());	
				 System.out.println("Sujeito: "+values[4]);
				 System.out.println("Email: " + values[5]);
			} catch (InvalidNameException | CertificateException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 
			  
		  }
				  
		 scanner.close();
			return false;
		}


		private static boolean checkPassword(String password) {
			boolean OK = true; 
			if(password.length()>8 || password.length()<5) {
				 System.out.println("– Senha deve ser <seis, sete ou oito dígitos> escreve de novo: ");
				 OK = false;
			}else {
				/*check other problems */ 
				for(int i = 0 ; i<password.length()-1 && OK; i ++) {
					if(password.charAt(i) == password.charAt(i+1) ) {
						/* consecutive repetitions*/
						System.out.println("– Senha  nao deve ter repetiçao de digitos consecutivos");
						OK = false;
					
						
					}else{
						int[] numbers = new int[3];
						numbers[0] = password.charAt(i);
						numbers[1] = password.charAt(i+1);

						if( (numbers[0] +1 )== numbers[1] || (numbers[0]-1) == numbers[1] ) {
							System.out.println("– Senha  nao deve ter  digitos em sequência crescente o decrescente");
							OK = false;	
						}	
					}
				}
			}
			
			return OK;
		}

}
