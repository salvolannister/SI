package mainpackage;

/* this class manage the secret arquives collected in the index file
 * 
 */
public class Arquive {
  private String secretName;
  private String name;
  private String groupName;
  private String dono;
  
	public String getSecretName() {
		return secretName;
	}
	public void setSecretName(String secretName) {
		this.secretName = secretName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDono() {
		return dono;
	}
	public void setDono(String dono) {
		this.dono = dono;
	}
  
	public Arquive (String line) {
		String[] values = line.split(" ");
		name = values[0];
		secretName = values[1];
		dono = values[2];
		groupName = values[3];
	  //System.out.println(name +" "+secretName+ " "+ dono+" "+ groupName);
	}
  
}
