package com.pismo.vertxexample.services

import br.com.six2six.fixturefactory.Fixture
import com.pismo.vertxexample.domain.Beer
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.fixtures.BeerFixture
import com.pismo.vertxexample.fixtures.BreweryFixture
import com.pismo.vertxexample.fixtures.FixtureLoader
import com.pismo.vertxexample.repositories.BeerRepository
import io.reactivex.Single
import spock.lang.Specification

class BeerServiceUnitSpec extends Specification {

    BeerService beerService = new BeerService()

    def setup() {
        FixtureLoader.loadTemplates()
    }
    
    def 'Should create a beer'() {
        given:
        Brewery brewery = Fixture.from(Brewery).gimme(BreweryFixture.DEFAULT)
        Beer beer = Fixture.from(Beer).gimme(BeerFixture.WITHOUT_BREWERY)
        
        and:
        BreweryService breweryService = Mock(BreweryService)
        beerService.breweryService = breweryService
        
        breweryService.findByName(brewery.name) >> {
            Single.just(brewery)
        }
        
        and:
        BeerRepository beerRepository = Mock(BeerRepository)
        beerService.beerRepository = beerRepository
        
        beerRepository.insert({ it.brewery == brewery } as Beer) >> {
            Single.just(beer)
        }
        
        when:
        Beer inserted = 
                beerService
                        .createBeer(brewery.name, beer)
                        .blockingGet()
        
        beer.brewery = brewery
        
        then:
        inserted == beer
    }
    
}
