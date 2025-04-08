package src.Services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import src.models.Device;
import org.springframework.beans.factory.annotation.Autowired;
import src.repos.DeviceRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceServices {

  @Autowired
  DeviceRepository deviceRepository;

  public void save(Device device){
    deviceRepository.save(device);
  }

  public void deleteById(Device device){
    if(deviceRepository.existsById((long) device.getId())){
      deviceRepository.deleteById((long) device.getId());
    }
  }

  public List<Device> findAll(int userId){
    return deviceRepository.find(userId);
  }

  public List<String> fetchFcmTokens (int id){
    return deviceRepository.find(id).stream().map(Device :: getFcmToken)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  public Boolean deleteByFcmToken(List<String> tokens){
    try{
      deviceRepository.deleteFcmTokens(tokens);
      return true;
    }catch (Exception e){
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }

  public Device findByFcmToken(String token){
    try{
      return deviceRepository.findToken(token);
    } catch (Exception e) {
      throw new RuntimeException(e.getLocalizedMessage());
    }
  }
}
