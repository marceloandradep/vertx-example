package com.pismo.vertxexample.repositories

import br.com.six2six.fixturefactory.Fixture
import com.ninja_squad.dbsetup.DbSetupTracker
import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.fixtures.BreweryFixture
import com.pismo.vertxexample.processors.BreweryProcessor
import com.pismo.vertxexample.tests.DBIntegrationBase
import org.assertj.db.type.Table
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import javax.sql.DataSource

import static org.assertj.db.api.Assertions.assertThat

class BreweryRepositoryIntegrationSpec extends DBIntegrationBase {

    private static DbSetupTracker dbSetupTracker = new DbSetupTracker()

    private BreweryProcessor breweryProcessor
    
    private Table breweriesTable
    
    @Autowired
    private DataSource dataSource

    @Autowired
    BreweryRepository breweryRepository

    @PostConstruct
    void setup() {
        breweryProcessor = new BreweryProcessor(dataSource, dbSetupTracker)

        breweriesTable = new Table(dataSource, 'Breweries')
    }

    @Test
    def 'Should save a brewery'() {
        given:
        Brewery brewery = Fixture.from(Brewery).gimme(BreweryFixture.DEFAULT)

        when:
        Brewery persistedBrewery =
                breweryRepository
                        .insert(brewery)
                        .blockingGet()

        then:
        assertThat(breweriesTable)
                .hasNumberOfRows(1)
                .row()
                .value().isEqualTo(persistedBrewery.id)
                .value().isEqualTo(persistedBrewery.name)
                .value().isEqualTo(persistedBrewery.location)
    }

    @Test
    def 'Should find a register'() {
        given:
        Brewery brewery = 
                Fixture.from(Brewery)
                        .uses(breweryProcessor)
                        .gimme(BreweryFixture.DEFAULT)

        when:
        Brewery persistedBrewery =
                breweryRepository
                        .findByName(brewery.name)
                        .blockingGet()

        then:
        persistedBrewery == brewery
    }

}
