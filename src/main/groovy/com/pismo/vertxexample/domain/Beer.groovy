package com.pismo.vertxexample.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields = true, ignoreNulls = true, excludes = [ 'metaClass' ])
@EqualsAndHashCode
class Beer {

    static final POST_SCHEMA = '''
        {
            "type": "object",
            "properties": {
                "name": { "type": "string" },
                "style": { "type": "string" },
                "abv": { "type": "number" }
            },
            "required": [ "name", "style", "abv" ]
        }
    '''
    
    Long id
    
    String name
    String style
    Float abv
    
    Brewery brewery
    
}
