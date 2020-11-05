package beans;

public class Employee {
	
    private int id;
    
    private String name;
    
    private String department;
   
    private long salary;
    
    // constructors
	public Employee(int id, String name, String department, long salary) {
		this.id = id;
		this.name = name;
		this.department = department;
		this.salary = salary;
	}
 
    // standard getters and setters.
}
