package com.pismo.vertxexample.services

import com.pismo.vertxexample.domain.Beer
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.repositories.BeerRepository
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BeerService {
    
    @Autowired
    BreweryService breweryService
    
    @Autowired
    BeerRepository beerRepository
    
    Single<Beer> createBeer(String breweryName, Beer beer) {
        def payload = [
                breweryName: breweryName,
                beer: beer
        ]
        
        Single.just(payload)
                .flatMap(findBrewery)
                .flatMap(saveBeer)
                .map { Map result ->
                    result.beer
                }
    }
    
    def findBrewery = { Map payload ->
        String breweryName = payload.breweryName
        
        breweryService
                .findByName(breweryName)
                .map { Brewery brewery ->
                    Beer beer = payload.beer
                    beer.brewery = brewery
            
                    payload + [ beer: beer ]
                }
    }
    
    def saveBeer = { Map payload ->
        Beer beer = payload.beer
        
        beerRepository
                .insert(beer)
                .map {
                    payload + [ beer: beer ]
                }
    }
    
    Single<Beer> findByName(String breweryName, String beerName) {
        beerRepository
                .findByName(breweryName, beerName)
    }
    
}
