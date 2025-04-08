package src.Controllers;


import org.springframework.http.ResponseEntity;
import src.Services.UserServices;
import src.models.Device;
import src.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apis/user")
public class UserController {

  private final UserServices userServices;

  @Autowired
  public UserController(UserServices userServices) {
    this.userServices = userServices;
  }

  @PostMapping("/add")
  public ResponseEntity<User> createUser(@RequestBody User user){
    try{
      return userServices.save(user);
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/{id}/devices")
  public Device addDeviceToUser(@PathVariable int id, @RequestBody Device device) {
    try{
      return userServices.addDeviceToUser(id, device);
    }catch (Exception e){
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }
}
