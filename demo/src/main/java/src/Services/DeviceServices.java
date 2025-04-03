package src.Services;

import org.springframework.stereotype.Service;
import src.models.Device;
import org.springframework.beans.factory.annotation.Autowired;
import src.repos.DeviceRepository;

import java.util.List;

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
}
