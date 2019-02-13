package com.pismo.vertxexample.services


import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.repositories.BreweryRepository
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BreweryService {
    
    @Autowired
    BreweryRepository breweryRepository
    
    Single<Brewery> createBrewery(Brewery brewery) {
        breweryRepository.insert(brewery)
    }
    
    Single<Brewery> findByName(String name) {
        breweryRepository.findByName(name)
    }
    
}
