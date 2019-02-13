package com.pismo.vertxexample.repositories

import com.pismo.vertxexample.domain.Beer
import com.pismo.vertxexample.vertx.mysql.RxMysql
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BeerRepository {
    
    static final String INSERT = '''
        INSERT INTO Beers (
            Name, Style, ABV, Brewery_ID
        ) VALUES (
            ?, ?, ?, ?
        )
    '''
    
    static final String FIND_BY_NAME = '''
        SELECT
            be.Beer_ID id,
            be.Name name,
            be.Style style,
            be.ABV abv,
            br.Brewery_ID breweryId,
            br.Name breweryName,
            br.Location breweryLocation
            
        FROM Beers be
        
        JOIN Breweries br
            ON br.Brewery_ID = be.Brewery_ID
        
        WHERE br.Name = ? AND be.Name = ?
    '''

    @Autowired
    RxMysql rxMysql

    Single<Beer> insert(Beer beer) {
        def params = [ beer.name, beer.style, beer.abv, beer.brewery.id ]
        
        rxMysql
                .insert(INSERT, params)
                .map { Long id ->
                    beer.id = id
                    beer
                }
    }
    
    Single<Beer> findByName(String breweryName, String beerName) {
        def params = [ breweryName, beerName ]
        
        rxMysql
                .select(FIND_BY_NAME, params)
                .map { Map result ->
                    [
                            id: result.id,
                            name: result.name,
                            style: result.style,
                            abv: Float.parseFloat(result.abv),
                            brewery: [
                                    id: result.breweryId,
                                    name: result.breweryName,
                                    location: result.breweryLocation
                            ]
                    ] as Beer
                }
    }
    
}
