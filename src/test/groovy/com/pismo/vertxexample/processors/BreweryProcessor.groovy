package com.pismo.vertxexample.processors

import br.com.six2six.fixturefactory.processor.Processor
import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.DbSetupTracker
import com.ninja_squad.dbsetup.destination.DataSourceDestination
import com.pismo.vertxexample.domain.Brewery

import javax.sql.DataSource

import static com.ninja_squad.dbsetup.Operations.insertInto
import static com.ninja_squad.dbsetup.Operations.sequenceOf

class BreweryProcessor implements Processor {

    private DataSource dataSource
    private DbSetupTracker dbSetupTracker

    BreweryProcessor(DataSource dataSource, DbSetupTracker dbSetupTracker) {
        this.dataSource = dataSource
        this.dbSetupTracker = dbSetupTracker
    }

    @Override
    void execute(Object result) {
        if (result instanceof Brewery) {
            DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), insertBrewery(result))
            dbSetupTracker.launchIfNecessary(dbSetup)
        }
    }

    static insertBrewery(Brewery brewery) {
        sequenceOf(
                insertInto('Breweries')
                        .columns('Brewery_ID', 'Name', 'Location')
                        .values(brewery.id, brewery.name, brewery.location)
                        .build()
        )
    }
}
