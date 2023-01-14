package app.eni;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Etudiant {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String email;

}

@RepositoryRestResource
interface EtudiantRepository extends JpaRepository<Etudiant,Long>{

}
@Projection(name="fullEtudiant",types=Etudiant.class)
interface ProductInterface
{
public Long getId();
public String getName();
public String getEmail();
}
@SpringBootApplication
public class EtudiantServiceApplication extends SpringBootServletInitializer {
	@Autowired
	EtudiantRepository etudiantRepository;
	public static void main(String[] args) {
		SpringApplication.run(EtudiantServiceApplication.class, args);
	}	
}
@Controller
@RequestMapping("/etudiant/")

class EtudiantRestController{
	@Autowired
	EtudiantRepository etudiantRepository;
	public EtudiantRestController(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

	@GetMapping("list")
	//@ResponseBody
    public String listEtudiants(Model model) {
    	
    	
    	List<Etudiant> le = (List<Etudiant>) etudiantRepository.findAll();
    	if(le.size()==0) le = null;
        model.addAttribute("etudiants", le);       
        return "etudiant/listEtudiants";
        
       
    }
    
    @GetMapping("add")
    public String showAddEtudiantForm(Model model) {
    	Etudiant etudiant = new Etudiant();// object dont la valeur des attributs par defaut
   
    	model.addAttribute("etudiant", etudiant);
        return "etudiant/addEtudiant";
    }
    
    @PostMapping("add")
    public String addEtudiant( Etudiant etudiant, BindingResult result) {
        if (result.hasErrors()) {
            return "etudiant/addEtudiant";
        }
        etudiantRepository.save(etudiant);
        return "redirect:list";
    }

    
    @GetMapping("delete/{id}")
    public String deleteEtudiant(@PathVariable("id") long id, Model model) {

    	Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("l'Id de l'Etudiant est invalide:" + id));

    	etudiantRepository.delete(etudiant);

        return "redirect:../list";
    }
    
    
    @GetMapping("edit/{id}")
    public String showEtudiantFormToUpdate(@PathVariable("id") long id, Model model) {
    	Etudiant etudiant = etudiantRepository.findById(id)
            .orElseThrow(()->new IllegalArgumentException("l'Id de l'Etudiant est invalide:" + id));
        
        model.addAttribute("etudiant", etudiant);
        
        return "etudiant/updateEtudiant";
    }


    
    @PostMapping("update")
    public String updateEtudiant( Etudiant etudiant, BindingResult result) {
    	etudiantRepository.save(etudiant);
    	return"redirect:list";
    	
    }
    
    @GetMapping("show/{id}")
	public String showEtudiant(@PathVariable("id") long id, Model model) {
    	Etudiant etudiant = etudiantRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("l'Id de l'Etudiantest invalide:" + id));
		model.addAttribute("etudiant", etudiant);
		return "etudiant/showEtudiant";
	}


}