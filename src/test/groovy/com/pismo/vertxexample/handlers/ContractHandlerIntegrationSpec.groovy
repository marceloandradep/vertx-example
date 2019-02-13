package com.pismo.vertxexample.handlers

import br.com.six2six.fixturefactory.Fixture
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.fixtures.BreweryFixture
import com.pismo.vertxexample.services.BreweryService
import com.pismo.vertxexample.tests.HttpIntegrationBase
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class ContractHandlerIntegrationSpec extends HttpIntegrationBase {

    @Autowired
    RestTemplate restTemplate

    @Autowired
    BreweryHandler breweryHandler

    @Test
    void 'When getting a brewery should return status 200 and a brewery object on response'() {
        given:

        Brewery brewery = Fixture.from(Brewery).gimme(BreweryFixture.DEFAULT)

        BreweryService breweryService = Mock(BreweryService)
        breweryHandler.breweryService = breweryService

        breweryService.findByName(brewery.name) >> {
            Single.just(brewery)
        }

        when:
        ResponseEntity response = restTemplate.getForEntity("${urlBase()}/v1/breweries/${brewery.name}", String)
        def responseBody = new JsonObject((String)response.body).map

        then:
        response.statusCode == HttpStatus.OK
        responseBody == [
            id: brewery.id,
            name: brewery.name,
            location: brewery.location
        ]
    }

    @Test
    void 'When posting a contract request should return status 201 and the new created contract object on response'() {
        given:
        
        Brewery brewery = Fixture.from(Brewery).gimme(BreweryFixture.DEFAULT)

        BreweryService breweryService = Mock(BreweryService)
        breweryHandler.breweryService = breweryService

        breweryService.createBrewery(brewery) >> {
            Single.just(brewery)
        }

        when:
        String url = "${urlBase()}/v1/breweries"

        RequestEntity<Brewery> requestEntity =
                RequestEntity
                        .post(URI.create(url))
                        .body(brewery)

        ResponseEntity response = restTemplate.exchange(requestEntity, String)
        def responseBody = new JsonObject((String)response.body).map

        then:
        response.statusCode == HttpStatus.CREATED
        responseBody == [
                id: brewery.id,
                name: brewery.name,
                location: brewery.location
        ]
    }

}
