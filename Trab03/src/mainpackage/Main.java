package mainpackage;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.SignatureException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Main {

	public static void main(String[] args) {
		Database.setDatabase("./database/trabalho3.db");
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		boolean valid = false;
		while(!exit ) {
			System.out.print("###################################################\n"+
							   "		      LOGIN PAGE\n"+		   
							   "			 email:");
			String email = new String (scanner.nextLine());
			User u = new User() ;
			/* check from the db if the email is valid or not */
				try {
					boolean alreadyExist= Database.checkUserExistence(email) ;
					/* scoprire come leggere un resultSEt senzza che dia errore*/
					if(!alreadyExist) {
						System.out.println("You are not registred, ask the admin to register. conitnue:0 exit:1");
						String choice = new String(scanner.nextLine());
							if(choice.equals("1")) {
								exit = true;
								System.out.println("                       GOODBYE");
							}
					}else {
						/* crea instance di user*/
						u = new User(email);
						int block = u.getBlock();
						if(block == 0) {
						tryLog(u);
						}
						else {
							if(checkTime(u)) {
								u.block(0);
								u.setAttempt(0);
								tryLog(u);
							}else {
								System.out.println("You are still blocked");
							}
						}
						
					
					
					}
				} catch (SQLException | NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		}	
	}


	private static boolean checkTime(User u) {
		LocalDateTime dateTime = LocalDateTime.now();
		//String dataNow = dateTime.toString();
		
		//System.out.println("Checking time, now: "+dataNow );
		String time =u.getTime();
		LocalDateTime dateBlocked = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
		int diff = dateTime.getMinute() - dateBlocked.getMinute();
	
		return diff>2;
	}


	private static void tryLog(User u ) throws SQLException, NoSuchAlgorithmException {
	/* read the password and check if it is correct*/
	Scanner scanner = new Scanner(System.in);
	boolean valid =false;
	int count = u.getAttempt();
	String[] values = u.getPandS();
	/* continue until the password is correct or tentatives are less than 3*/
		while(!valid && count<3) {
			if( count!= 0) {
				System.out.print("###################################################\n"+
						         "  Wrong password, try again\n"	   
						   );			
			}
		ArrayList<ArrayList<String>> digited =PasswordChecker.RequestForPassword();
		valid = PasswordChecker.isPasswordValid(digited,values[1],values[0]);
		u.addAttempt(); /* maybe this should be moved elsewhere*/
		count ++;
		}
			/*password is correct*/
			if(valid) {
				/*3 ETAPA add one access*/
				count = 0;
				u.addAccess();
				/*verification of private key*/
				try {
					privateKeyVerification(u);
					if(u.getBlock() == 0) {
						/* show interface for group and usuarios*/
						int GID =u.getGID();
						  printHeader(u);
						  /*here the possible arquives will be memorized */
						  HashMap<Integer,Arquive> files = new HashMap<>();
						if(GID == 1) {
							/*admin interface and possible while*/
							printBodyOne(u);
							int option =printMainMenu();
							boolean ahead= false;
							switch(option) {
							case 1:
								while(!ahead) {
									printHeader(u);
									printBodyOne(u);
									ahead = printRegisterUser();
								}
								;
							case 2:
								printHeader(u);
								printBodyOne(u);
								printOptionTwo(u);
								/*going back to main menu*/
								;
							case 3:
								printHeader(u);
								//printBodyOneVersion2(u);
								printOptionThree(u, files);
								/*how do I calculate consultas*/
								;
							case 4:
								/*go to login page*/
								;
							}
						}else {
							/*user interface*/
//							printBodyOne(u,GID);
//							printBodyTwo(GID);
						}
					}
				} catch (CertificateException | InvalidNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}else {
				/*block user*/
				System.out.print("###################################################\n"+
						   		 "You have been blocked wait 2 minutes and try again\n" );
				u.block(1);
				
			}
			scanner.close();
	}


	private static void printOptionThree(User u, HashMap<Integer, Arquive> files) {
		Scanner sc= new Scanner(System.in);
		System.out.print("Caminho da pasta <campo com 255 caracteres>: ");
		String path = sc.nextLine();
		/* I don't check because it crushes with more than 255 so ..*/
		System.out.print("\n\nPress 2 to decrypt pasta do arquivio ");
		
		/*show different files of the user*/
		System.out.print("\n\nPress 0 for going back to MainPage:);");
		int dec= Integer.parseInt(sc.nextLine());
			if(dec == 0) {
				return;
			}else {
				
				/*controll validity of path after*/
				DecryptArquive Da = new DecryptArquive(path+"index", u.getPrk(),u.getPub());
				try {
						/* decrypting index*/
						byte [] arquiveText = Da.decrypt();
						String content = new String(arquiveText, "UTF-8");
						//System.out.println(content);
						
						String[] contentLines = content.split("\n");
						
						for(int i = 0; i<contentLines.length;i++) {
							System.out.println("Press "+(i+1)+"to access "+contentLines[i]);
							files.put(i, new Arquive (contentLines[i]));
						}
						System.out.println("\n\nPress 0 to exit");
						
						dec =Integer.parseInt(sc.nextLine());
							if(dec!=0) {
								Arquive p = files.get(dec);
								/*if the user is not enabled ... do something*/
								int gid = getGID(p.getGroupName());
								if(p.getDono().equals(u.getEmail()) || u.getGID() ==gid) {
									/* the owner can read is file*/
									Da = new DecryptArquive(path+p.getSecretName(), u.getPrk(),u.getPub());
									/* notificare la presenza di alcuni errori mentre si decripta*/
									byte[] byteContent = Da.decrypt();
									String stringContent = new String(byteContent, "UTF-8");
									System.out.println("CONTENT :"+stringContent);
									/* create a new file*/
									PrintWriter writer = new PrintWriter(p.getName()+".txt", "UTF-8");
									writer.println(stringContent);
									/* what now? stay on the page? */
								}else {
									System.out.println("you can't access this file");
									/* what now? stay on the page? */
								}
							}
						
						
						} catch ( NoSuchAlgorithmException | NoSuchPaddingException
								| IllegalBlockSizeException | BadPaddingException |SignatureException | NoSuchProviderException  e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}catch (InvalidKeyException e) {
							System.out.println("Private or Public key is wrong");
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
			}
		return;
	}


	private static int getGID(String groupName) {
		if(groupName.equals("usuario"))
			return 0;
		return 1;
	}


	private static void printOptionTwo(User u) throws NoSuchAlgorithmException, SQLException {
		Scanner sc = new Scanner(System.in);
		String password = new String() ;
		boolean OK = false;
		System.out.print("Caminho do certificado digital: <campo com 255 caracteres> \r\n");
		String path = sc.nextLine();
		while(!OK) {
		System.out.print("– Senha pessoal:  \r\n");
	   // String password = PasswordChecker.readPassword("– Senha pessoal: ");
		 password = sc.nextLine();
		checkPassword(password);
		System.out.print("–Confirm senha pessoal:  \r\n");
		String checkPassword = sc.nextLine();
		
		if(checkPassword.equals(password))
			OK = true;
		}
		Database.updatePassword(u, password, path);
//			
		System.out.println("Going back to Main Menu");
		sc.close();
	}


	private static boolean printRegisterUser() throws InvalidNameException, CertificateException, SQLException, NoSuchAlgorithmException {
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
		 int gid = Integer.parseInt(scanner.nextLine());
		 while(gid!= 0 && gid !=1) {
			 System.out.println("Grupo pode ser User=0 ou Admin=1 ");
			 System.out.print("\nEscreve de novo:  ");
			 gid = Integer.parseInt(scanner.nextLine());
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
		  System.out.println("Press 1 para continuar Press 0 para voltar no menu principal 1");
		  int n = Integer.parseInt(scanner.nextLine());
		  if(n == 1) {
			  System.out.print("###################################################\n");
			  byte[] certificate = Arquives.ReadArquive(Paths.get(path));
			  String[] values = Arquives.readCertificate(certificate);
			  /* instead of receving a String values, the best way could be using a class*/
			 X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
			 x509Certificate = X509Certificate.getInstance(certificate);
			 System.out.println("Version: "+x509Certificate.getVersion());
			 System.out.println("Serial number: "+x509Certificate.getSerialNumber().toString());
			 System.out.println("Certificate valid before: "+x509Certificate.getNotBefore().toString()+
					 			" Certificate valid after: "+ x509Certificate.getNotAfter().toString());
			 System.out.println("Signature algorithm: "+x509Certificate.getSigAlgName().toString());
			 System.out.println("Issuer info: "+ x509Certificate.getIssuerDN().getName());	
			 System.out.println("Sujeito: "+values[4]);
			 System.out.println("Email: " + values[5]);
			 System.out.println("Press 1 para continuar Press 0 para voltar no menu principal 1");
			 n = Integer.parseInt(scanner.nextLine());
			 
			 if(n == 1) {
				 /*register the user in the database yeaaaa*/
				boolean alreadyExist= Database.checkUserExistence(values[5]) ;
				if(alreadyExist) {
					System.out.println("This user already exist!");
					System.out.println("Dados prenenchido");
					System.out.print("Path: "+path+"\n");
					System.out.print("Group: "+gid+"\n");
					System.out.println("Press 1:continuar com cadastro, Press 0: voltar no menu principal");
				}else {
					User b = new User();
					b.registerUser(path, gid, password);
					System.out.println("User registered with success");
					System.out.println("Press 1:continuar com cadastro, Press 0: voltar no menu principal");
				}
			 if(Integer.parseInt(scanner.nextLine()) == 1)
				 return true;
			 
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


	private static int printMainMenu() {
		
		System.out.println("Menu Principal:");
		System.out.println("");
		System.out.println("1 - Cadastrar um novo usuário");
		System.out.println("2 – Alterar senha pessoal e certificado digital do usuário");
		System.out.println("3 – Consultar pasta de arquivos secretos do usuário");
		System.out.println("4 – Sair do Sistema");
		Scanner scanner = new Scanner(System.in);
		String result = new String(scanner.nextLine());
		scanner.close();
		return Integer.parseInt(result);
	}


	private static void printBodyOne(User u) {
		System.out.println("Total access: "+ u.getAccess());
	}


	private static void printHeader(User u) {
		System.out.print("###################################################\n"+
						 "Login: "+u.getEmail()+
						 "\nGrupo: "+ u.getGroupName()+
						 "\nName: "+u.getName());
	}


	private static void privateKeyVerification(User u) throws CertificateException, SQLException {
		// DA VERIFICARE SE BLOCCA VERAMENTE O CRASCIA TUTTO
		int attempt = 0;
		boolean OK = false;
		Scanner scanner = new Scanner(System.in);
		while(attempt< 3  && !OK) {
			
			System.out.print("###################################################\n"+
					   "Working Directory is"+ System.getProperty("user.dir")+		   
					   "\n	Bynary file path:");
		
			String binFilepath = new String (scanner.nextLine());
			System.out.print("###################################################\n"+
					   "password:");	   
			String secretPhrase = new String (scanner.nextLine());
			
			
			System.out.print("###################################################\n"+
					   "\n	certificate path:");
			String certifPath = new String (scanner.nextLine());
			
			OK = PrivateKeyVerification.CheckPrivateKey(new String[] {binFilepath,secretPhrase,certifPath}, u);
		}
		if(!OK) {
			System.out.print("###################################################\n"+
					   "You have been blocked, wait 2 minutes and try again\n" );
			u.block(1);
		}
		scanner.close();
	}

	
	
}
