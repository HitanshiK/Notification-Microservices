package src.Services;

import org.springframework.stereotype.Service;
import src.models.Device;
import org.springframework.beans.factory.annotation.Autowired;
import src.repos.DeviceRepository;

import java.util.List;
import java.util.Objects;

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
            .toList();
  }
}
