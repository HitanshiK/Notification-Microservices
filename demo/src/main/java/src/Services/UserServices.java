package src.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import src.models.Device;
import src.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import src.repos.DeviceRepository;
import src.repos.UserRepository;

@Service
public class UserServices {

  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;

  @Autowired
  public UserServices(UserRepository userRepository,DeviceRepository deviceRepository) {
    this.userRepository=userRepository;
    this.deviceRepository=deviceRepository;
  }

  @Autowired
  EventTriggerService eventTriggerService;


  public ResponseEntity<User> save(User user){
     userRepository.save(user);
     eventTriggerService.triggerUserRegistered(user.getEmail());
     return ResponseEntity.ok(userRepository.save(user));
  }

  public void deleteById(User user){
    if(userRepository.existsById(user.getId())){
      userRepository.deleteById(user.getId());
    }
  }

  public Device addDeviceToUser(int id,Device device){
    return userRepository.findById(id).map(user -> {
      device.setUser(user);
      if(deviceRepository.existsById((long) device.getId())) {
        //saving new fcm token
        Device oldDevice = new Device();
        oldDevice.setFcmToken(device.getFcmToken());
        oldDevice.setLastUsedAt(System.currentTimeMillis());
        return deviceRepository.save(oldDevice);
      }else{
        return deviceRepository.save(device);
      }
    }).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public Boolean validateUser(String email){
    User user = userRepository.findByEmail(email);
    if(user == null){
      return false;
    }
    return true;
  }

  public User findUser(String email){
    return userRepository.findByEmail(email);
  }
}
