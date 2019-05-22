package mainpackage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.InvalidNameException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Main {

	public static void main(String[] args) {
		Database.setDatabase("./database/trabalho3.db");
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		while(!exit ) {
			System.out.print("###################################################\n"+
							   "		      LOGIN PAGE\n"+		   
							   "			 email:");
			String email = new String (scanner.nextLine());
			Database.addLog(1001);
			Database.addLog(2001);
			User u = new User() ;
			/* check from the db if the email is valid or not */
				try {
					boolean alreadyExist= Database.checkUserExistence(email) ;
					/* scoprire come leggere un resultSEt senzza che dia errore*/
					if(!alreadyExist) {
						Database.addLog(2005,email);
						System.out.println("You are not registred, ask the admin to be registered. conitnue:0 exit:1");
						String choice = new String(scanner.nextLine());
							if(choice.equals("1")) {
								exit = true;
								System.out.println("                       GOODBYE");
								Database.addLog(2002);
								Database.addLog(1002);
								
							}
					}else {
						/* crea instance di user*/
						u = new User(email);
						int block = u.getBlock();
						
						if(block == 0) {
							Database.addLog(2003,u.getEmail());
							Database.addLog(2002);
							
							tryLog(u);
						}
						else {
							
							if(checkTime(u)) {
								Database.addLog(2003,u.getEmail());
								Database.addLog(2002);
								u.block(0);
								u.setBlock(0);
								u.setAttempt(0);
								tryLog(u);
							}else {
								Database.addLog(2004,u.getEmail());
								System.out.println("You are still blocked");
							}
						}
						
					
					
					}
				} catch (SQLException | NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		}
		try {
			Database.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scanner.close();
	}


	private static boolean checkTime(User u) {
		LocalDateTime dateTime = LocalDateTime.now();
		//String dataNow = dateTime.toString();
		
		//System.out.println("Checking time, now: "+dataNow );
		String time =u.getTime();
		LocalDateTime dateBlocked = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
		ZoneOffset zoneOffSet= ZoneOffset.of("+02:00");
//		System.out.println(" now "+dateTime.toEpochSecond(zoneOffSet)+ " before "+dateBlocked.toEpochSecond(zoneOffSet));
		long diff = dateTime.toEpochSecond(zoneOffSet)-dateBlocked.toEpochSecond(zoneOffSet);
	
//		System.out.println("difference is "+diff);
		return diff>140;
	}


	private static void tryLog(User u ) throws SQLException, NoSuchAlgorithmException {
	/* read the password and check if it is correct*/
	Database.addLog(3001,u.getEmail());
	boolean valid =false;
	int count = 0;

	String[] values = u.getPandS();
	/* continue until the password is correct or tentatives are less than 3*/
		while(!valid && count<3) {
			if( count!= 0) {
				System.out.print("###################################################\n"+
						         "           Wrong password, try again\n"	   
						   );			
			}
		ArrayList<ArrayList<String>> digited =PasswordChecker.RequestForPassword();
		/* to be added <5 */
		if(digited.size()>8 || digited.size()<6 ) {
			System.out.println("Password must be length must be between 8 and 6 digits");
			valid  =false;
			
		}
		else {
		valid = PasswordChecker.isPasswordValid(digited,values[1],values[0]);
		}
     	u.addAttempt(); /* maybe this should be moved elsewhere*/
		count ++;
		if(valid == false) {
			switch(count) {
			case 1:
				Database.addLog(3004,u.getEmail());
			case 2:
				Database.addLog(3005,u.getEmail());
			case 3:
				Database.addLog(3006,u.getEmail());
			}
		}
//		valid = true;
		}
			/*password is correct*/
			if(valid) {
				Database.addLog(3003,u.getEmail());
				Database.addLog(3002,u.getEmail());
				
				
				count = 0;
				u.setAttempt(0);
				u.addAccess();
				/*verification of private key*/
				
				try {
					/*3 ETAPA privateKey verification*/
					Database.addLog(4001,u.getEmail());
					privateKeyVerification(u);
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(u.getBlock()== 0)
					{
					/*interface moment*/
					
					Database.addLog(4003,u.getEmail());
					Database.addLog(4002,u.getEmail());
					thirdStep(u);
					}else {
					
						Database.addLog(4002,u.getEmail());
						return;
					}
			
			}else {
				/*block user*/
				Database.addLog(3007,u.getEmail());
				Database.addLog(3002,u.getEmail());
				
				System.out.print("###################################################\n"+
						   		 "You have been blocked wait 2 minutes and try again\n" );
				u.block(1);
				u.setBlock(1);
				return;
			}
		
		
	}
	
	private static void thirdStep(User u) throws SQLException {
		
		int GID =u.getGID();
		int decision = 1;
		HashMap<Integer,Arquive> files = new HashMap<>();
		/* ripeti fin quando l'user non chiede di uscire*/
		while(decision == 1) { 
			printHeader(u);
		
		
				

				  /*here the possible arquives will be memorized */
				 
					printTotalAccess(u);
						int option =printMainMenu(GID);
						Database.addLog(5001,u.getEmail());
						/* show interface for group and usuarios*/
						if(GID == 1) {
							decision =adminInterface(option,u,files);
						}else {
						/*user interface*/
							decision =userInterface(option,u,files);
						}
		}
	}

	private static int adminInterface(int option, User u, HashMap<Integer, Arquive> files2) {
		
		boolean ahead = false;
			switch(option) {
			case 1:
				Database.addLog(5002,u.getEmail());
				while(!ahead) {
					printHeader(u);
					printTotalUser();
					try {
						try {
							Database.addLog(6001, u.getEmail());
							ahead = printRegisterUser(u);
						} catch (InvalidNameException | CertificateException | SQLException e) {
							// TODO Auto-generated catch block
							ahead = false;
							e.printStackTrace();
						}
					} catch (NoSuchAlgorithmException e) {
						ahead = false;
						e.printStackTrace();
					}
				}
				return 1;
			case 2:
				Database.addLog(5003,u.getEmail());
				printHeader(u);
				printTotalAccess(u);
				try {
					Database.addLog(7001, u.getEmail());
					while(printOptionPassword(u));
				} catch (NoSuchAlgorithmException | SQLException e) {
					
					e.printStackTrace();
				}
				return 1;
				/*going back to main menu*/
				
			case 3:
				boolean continuar = true;
				Database.addLog(5004,u.getEmail());
				while(continuar) {
					
				
				printHeader(u);
				//printBodyOneVersion2(u);
				continuar =printOptionPasta(u, files2);
				}
				/*Body 2:how do I calculate consultas*/
				return 1;
				
			case 4:
				/*go to login page*/
				Database.addLog(5005,u.getEmail());
				printHeader(u);
				printTotalAccess(u);
				return printExitInteface(u);
				
				
		}
			return 0;
	}
	
	private static void printTotalUser() {
		int totUser= 0;
		try {
			totUser = Database.getTotalAccess();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("###################################################\n");
		System.out.println("Total number of system's user:"+totUser);
		
	}


	private static int userInterface(int option, User u, HashMap<Integer, Arquive> files) {
		
		boolean continuar = true;
		switch(option) {
		case 1:
				Database.addLog(5001,u.getEmail());
				printHeader(u);
				printTotalAccess(u);
			try {
				/*option two is option 1 for user*/
				Database.addLog(7001, u.getEmail());
				while(printOptionPassword(u));
			} catch (NoSuchAlgorithmException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return 1;
		case 2:
			Database.addLog(5002,u.getEmail());
			while(continuar) {
				printHeader(u);
			/*Body2 :how do I calculate consultas*/
			continuar =printOptionPasta(u, files);
			}
			return 1;
		case 3:
			Database.addLog(5003,u.getEmail());
			printHeader(u);
			printTotalAccess(u);
			return printExitInteface(u);
	}
		return 0;
	}

	private static int printExitInteface(User u) {
		Database.addLog(9001, u.getEmail());
		Scanner sc = new Scanner(System.in);
		System.out.println("                                GOODBYE");
		System.out.println("Press 0 to exit or 1 to go to MainMenu:");
		int d=  Integer.parseInt(sc.nextLine());
		if(d == 0) {
			Database.addLog(9003,u.getEmail());
			Database.addLog(1002);
			System.out.println("                            BYE,BYE");
			System.exit(0);
		}
		Database.addLog(9004,u.getEmail());
		return d;
	}


	private static boolean printOptionPasta(User u, HashMap<Integer, Arquive> files) {
		Database.addLog(8001, u.getEmail());
		Scanner sc= new Scanner(System.in);
		System.out.print("Caminho da pasta <campo com 255 caracteres>:");
		String path = sc.nextLine();
		while(Files.exists(Paths.get(path)) == false) {
			Database.addLog(8004,u.getEmail());
			
			System.out.print("Caminho da pasta invalido escerever de novo:");
		    path = sc.nextLine();
		}
		/* I don't check because it crushes with more than 255 so ..*/
		System.out.print("\n\nPress 2 to decrypt pasta do arquivio");
		
		/*show different files of the user*/
		System.out.print("\n\nPress 0 for going back to MainPage:");
		int dec= Integer.parseInt(sc.nextLine());
			if(dec == 0) {
				Database.addLog(8002, u.getEmail());
				return false;
			}else {
				Database.addLog(8003, u.getEmail());
				
				DecryptArquive Da = new DecryptArquive(path+"index", u.getPrk(),u.getPub(),0, u);
				try {
						/* decrypting index*/
					byte [] arquiveText = Da.decrypt();
					if(arquiveText!= null) {
						Database.addLog(8009, u.getEmail());
						String content = new String(arquiveText, "UTF-8");
						//System.out.println(content);
						Database.addLog(8005, u.getEmail());
						String[] contentLines = content.split("\n");
						Database.addLog(8006, u.getEmail()); /*mmm*/
						for(int i = 0; i<contentLines.length;i++) {
							System.out.println("Press "+(i+1)+" to access "+contentLines[i]);
							files.put(i, new Arquive (contentLines[i]));
						}
						System.out.println("\n\nPress 0 to exit:");
						
						dec =Integer.parseInt(sc.nextLine());
							if(dec!=0) {
								Arquive p = files.get(dec-1);
								Database.addLog(8010, u.getEmail(),p.getName());
								/*if the user is not enabled ... do something*/
								int gid = getGID(p.getGroupName());
								if(p.getDono().equals(u.getEmail()) || u.getGID() ==gid) {
									/* the owner can read is file*/
									Database.addLog(8011, u.getEmail(),p.getName());
									Da = new DecryptArquive(path+p.getName(), u.getPrk(),u.getPub(),1,u);
									/* notificare la presenza di alcuni errori mentre si decripta*/
									byte[] byteContent = Da.decrypt();
									if(byteContent != null) {
										
										/* create a new file*/
										FileOutputStream fos = new FileOutputStream(path + "\\" +p.getSecretName());
										try {
											fos.write(byteContent);
										} catch (IOException e) {
											System.out.println("Arquivo nao fui salvad com Sucesso!");
											e.printStackTrace();
										}
										System.out.println("Arquivo Salvo com Sucesso!");
									}
									/* what now? stay on the page? YES*/
								}else {
									System.out.println("you can't access this file");
									Database.addLog(8012, u.getEmail(),p.getName());
									/* what now? stay on the page? */
								}
								System.out.println("\n\nPress 0 to exit or 1 to continue reading:");
								
								dec =Integer.parseInt(sc.nextLine());
								if(dec!=0) {
									return true;
								}
								return false;
							}
							return false;
						
					}else {
						System.out.println("\n\nPress 0 to exit or 1 to continue reading:");
						
						dec =Integer.parseInt(sc.nextLine());
						if(dec!=0) {
							return true;
						}
						return false;
					}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				
			
//			sc.close();
		return false;
	}


	private static int getGID(String groupName) {
		if(groupName.equals("usuario"))
			return 0;
		return 1;
	}


	private static boolean printOptionPassword(User u) throws NoSuchAlgorithmException, SQLException {
		Scanner sc = new Scanner(System.in);
		String password = new String() ;
		boolean OK = false;
		System.out.print("Caminho do certificado digital <campo com 255 caracteres>:");
		String path = sc.nextLine();
		while(Files.exists(Paths.get(path)) == false) {
			System.err.print("\npath is not valid insert again:");
			System.out.print("Caminho do certificado digital <campo com 255 caracteres>:");
			 path = sc.nextLine();
			 Database.addLog(7003,u.getEmail());
		}
		
		while(!OK) {
		System.out.print("\n– Senha pessoal:");
	   // String password = PasswordChecker.readPassword("– Senha pessoal: ");
		 password = sc.nextLine();
		OK =checkPassword(password);
		if(OK) {
			
		System.out.print("\n–Confirm senha pessoal:");
		String checkPassword = sc.nextLine();
		
			if(checkPassword.equals(password)) {
				
				Database.addLog(7004, u.getEmail());
				OK = true;
			
			}else {
				Database.addLog(7005, u.getEmail());
				System.out.println("The two passwords don't correspond");
				OK = false;
			}
		}else {
			Database.addLog(7002, u.getEmail());
			System.out.println("---try again---");
		}
		
		}
		
		Database.updatePassword(u, password, path);
		Scanner scanner = new Scanner(System.in);	
		System.out.println("Going back to Main Menu");
		 System.out.println("Press 1 para continuar Press 0 para voltar no menu principal");
		  
		int n = Integer.parseInt(scanner .nextLine());
		  if(n == 1) {
			  return true;
		  }else {
			  Database.addLog(7006, u.getEmail());
			  return false;
		  }
//		
	}


	private static boolean printRegisterUser(User u) throws InvalidNameException, CertificateException, SQLException, NoSuchAlgorithmException {
		 Scanner scanner = new Scanner(System.in);
		 System.out.println("");
		 System.out.println("Formulário de Cadastro:");
		 System.out.println("");
		 System.out.print("– Caminho do arquivo do certificado digital<campo com 255 caracteres>:");
		 String path = new String(scanner.nextLine());
		 while (path.length() > 255) {
			 System.out.println("caminho deve ser max 255 caaracteres, insert again:");
			 path = new String(scanner.nextLine());
			 
		 }
		 byte[] certificate = Arquives.ReadArquive(Paths.get(path));
		 while(certificate == null ) {
			 System.out.println("path errado, insert again: ");
			  Database.addLog(6004, u.getEmail());
			 path = new String(scanner.nextLine());
			 certificate = Arquives.ReadArquive(Paths.get(path));
		 }
		
		 System.out.print("\n– Grupo User=0 Admin=1:");
		 int gid = Integer.parseInt(scanner.nextLine());
		 while(gid!= 0 && gid !=1) {
			 System.out.println("Grupo pode ser User=0 ou Admin=1 ");
			 System.out.print("\nEscreve de novo:");
			 gid = Integer.parseInt(scanner.nextLine());
		 }
		 
		 String[] values = Arquives.readCertificate(certificate);
		 
//		 System.out.println("– Senha pessoal<seis, sete ou oito dígitos>:");
//		 
	 String password = null;
		 boolean OK = false;
//		  while(!OK ) {
//			     Database.addLog(6003, u.getEmail());
//			     System.out.print("\nEsreve de novo a password: ");
//			     password = new String(scanner.nextLine());
//			  	 OK = checkPassword(password);
//				
//		  }
		  /*COPIARE*/
		 while(!OK) {
				System.out.print("\n– Senha pessoal:");
			   // String password = PasswordChecker.readPassword("– Senha pessoal: ");
				 password = scanner.nextLine();
				OK =checkPassword(password);
				if(OK) {
					
				System.out.print("\n–Confirm senha pessoal:");
				String checkPassword = scanner.nextLine();
				
					if(checkPassword.equals(password)) {
						
						
						OK = true;
					
					}else {
						
						System.out.println("The two passwords don't correspond");
						OK = false;
					}
				}else {
					 Database.addLog(6003, u.getEmail());
					
					System.out.println("---try again---");
				}
		 }
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  System.out.println("Press 1 para continuar Press 0 para voltar no menu principal");
		  int n = Integer.parseInt(scanner.nextLine());
		  if(n == 1) {
			 Database.addLog(6002, u.getEmail());
			 System.out.print("###################################################\n");
			  
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
			 Database.addLog(6002, values[5]);
			 System.out.println("Press 1 para continuar, Press 0 para voltar no menu principal:");
			 n = Integer.parseInt(scanner.nextLine());
			 
			 if(n == 1) {
				 
				 /*register the user in the database yeaaaa*/
				boolean alreadyExist= Database.checkUserExistence(values[5]) ;
				if(alreadyExist) {
					Database.addLog(6006, values[5]);
					System.out.println("This user already exist!");
					System.out.println("Dados prenenchido");
					System.out.print("Path: "+path+"\n");
					System.out.print("Group: "+gid+"\n");
					System.out.println("Press 1:continuar com cadastro, Press 0: voltar no menu principal");
				}else {
					Database.addLog(6005, values[5]);
					User b = new User();
					b.registerUser(path, gid, password);
					System.out.println("User registered with success");
					System.out.println("Press 1:continuar com cadastro, Press 0: voltar no menu principal");
				}
				System.out.print("Choise:");
			 if(Integer.parseInt(scanner.nextLine()) == 1)
				 return false; /*it will not go ahead it will stay in the cadatro menu*/
			 
			 }else {
				 Database.addLog(6007, values[5]);
				 return true;
			 }
		  }
				  
//		 scanner.close();
		  	
			return true;
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


	private static int printMainMenu(int GID) {
		
		if(GID == 1) {
		System.out.println("                               Menu Principal \n\n");
		System.out.println("");
		System.out.println("1 - Cadastrar um novo usuário");
		System.out.println("2 – Alterar senha pessoal e certificado digital do usuário");
		System.out.println("3 – Consultar pasta de arquivos secretos do usuário");
		System.out.println("4 – Sair do Sistema");
		}else {
			System.out.println("                           Menu Principal\n\n");
		
		
			System.out.println("1 – Alterar senha pessoal e certificado digital do usuário");
			System.out.println("2 – Consultar pasta de arquivos secretos do usuário");
			System.out.println("3 – Sair do Sistema");
		}
		System.out.println(" Make a choice: ");
		Scanner scanner = new Scanner(System.in);
		String result = new String(scanner.nextLine());
//		scanner.close();
		return Integer.parseInt(result);
	}


	private static void printTotalAccess(User u) {
		System.out.println("Total access: "+ u.getAccess());
	}


	private static void printHeader(User u) {
		System.out.print("###################################################\n"+
						 "Login: "+u.getEmail()+
						 "\nGrupo: "+ u.getGroupName()+
						 "\nName: "+u.getName()+"\n");
	}


	private static void privateKeyVerification(User u) throws CertificateException, SQLException {
		// DA VERIFICARE SE BLOCCA VERAMENTE O CRASCIA TUTTO
		int attempt = 0;
		boolean OK = false;
		Scanner scanner = new Scanner(System.in);
		u.setAttempt(0);
		while(attempt< 3  && !OK) {
			String str = new String();
			System.out.print("###################################################\n"+
					   "Working Directory is"+ System.getProperty("user.dir")+		   
					   "\n	Binary file path:");
			str = scanner.nextLine();
			String binFilepath = new String (str);
			
			while(Files.exists(Paths.get(binFilepath)) == false) {
				System.err.print("FILE DOESN'T EXIST, try again \n");
				System.out.print("###################################################\n"+
						   "Working Directory is"+ System.getProperty("user.dir")+		   
						   "\n	Binary file path:");
				str = scanner.nextLine();
			    binFilepath = new String (str);
			    Database.addLog(4004,u.getEmail());
			}
			
			
			System.out.print("###################################################\n"+
					   "password:");	   
			String secretPhrase = new String (scanner.nextLine());
			
			
//			System.out.print("###################################################\n"+
//					   "\n	certificate path:");
//			String certifPath = new String (scanner.nextLine());
			
			OK = PrivateKeyVerification.CheckPrivateKey(new String[] {binFilepath,secretPhrase}, u);
			u.addAttempt();
			attempt ++ ;
		}
		if(!OK) {
			Database.addLog(4007,u.getEmail());
			System.out.print("###################################################\n"+
					   "You have been blocked, wait 2 minutes and try again\n" );
			u.block(1);
			u.setBlock(1);
		}
//		scanner.close();
	}

	
	
}
