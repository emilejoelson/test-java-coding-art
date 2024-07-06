package ma.codingart.testjava.service.implementation;

import ma.codingart.testjava.service.EntityNameService;
import org.springframework.stereotype.Service;

@Service
public class EntityNameServiceImpl implements EntityNameService {
    @Override
    public String getEntityName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
