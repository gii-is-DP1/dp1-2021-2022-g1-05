package org.springframework.samples.petclinic.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
	
	
	  @GetMapping({"/","/welcome"})
	  public String welcome(Map<String, Object> model) {	    
		List<Person> lista = new ArrayList<>();
		Person person = new Person();
		person.setFirstName("Javier");
		person.setLastName(" Terroba Orozco");
		lista.add(person);
		Person person2 = new Person();
		person2.setFirstName("Manuel");
		person2.setLastName(" Padilla Tabuenca");
		lista.add(person2);
		Person person3 = new Person();
		person3.setFirstName("Mario");
		person3.setLastName(" Espinosa Rodríguez");
		lista.add(person3);
		Person person4 = new Person();
		person4.setFirstName("Juanjo");
		person4.setLastName(" Casamitjana Benítez");
		lista.add(person4);
		model.put("persons",lista);
		model.put("title","PetClinic");
		model.put("group","G1-05");
		
	    return "welcome";
	  }
}
