package com.pismo.vertxexample.repositories

import com.pismo.vertxexample.domain.Brewery
import com.pismo.vertxexample.vertx.mysql.RxMysql
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BreweryRepository {
    
    static final String INSERT = '''
        INSERT INTO Breweries (
            Name, Location
        ) VALUES (
            ?, ?
        )
    '''
    
    static final String FIND_BY_NAME = '''
        SELECT
            Brewery_ID id,
            Name name,
            Location location
        FROM Breweries
        WHERE Name = ?
    '''

    @Autowired
    RxMysql rxMysql

    Single<Brewery> insert(Brewery brewery) {
        def params = [ brewery.name, brewery.location ]
        
        rxMysql
                .insert(INSERT, params)
                .map { Long id ->
                    brewery.id = id
                    brewery
                }
    }
    
    Single<Brewery> findByName(String name) {
        def params = [ name ]
        
        rxMysql
                .select(FIND_BY_NAME, params)
                .map { Map result ->
                    [
                            id: result.id,
                            name: result.name,
                            location: result.location
                    ] as Brewery
                }
    }
    
}
