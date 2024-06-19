package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.models.USD;
import com.example.productsfromusa.repositories.AdminRepository;
import com.example.productsfromusa.repositories.USDRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class USDService {
    private final USDRepository usdRepository;

    public USDService(USDRepository usdRepository) {
        this.usdRepository = usdRepository;
    }

    public void save(USD usd) {
        usdRepository.save(usd);
    }

    public USD getCourse() {
        Iterable<USD> iterable = usdRepository.findAll();
        List<USD> list = new ArrayList<>();
        iterable.forEach(list::add);

        if(list.size() != 0) {
            return list.get(0);
        }
        return null;
    }
}
