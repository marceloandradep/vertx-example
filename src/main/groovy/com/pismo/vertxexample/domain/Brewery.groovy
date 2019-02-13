package com.pismo.vertxexample.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields = true, ignoreNulls = true, excludes = [ 'metaClass' ])
@EqualsAndHashCode
class Brewery {

    static final POST_SCHEMA = '''
        {
            "type": "object",
            "properties": {
                "name": { "type": "string" },
                "location": { "type": "string" }
            },
            "required": [ "name", "location" ]
        }
    '''
    
    Long id
    
    String name
    String location
    
}
