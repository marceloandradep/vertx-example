package com.pismo.vertxexample.fixtures

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader
import com.pismo.vertxexample.domain.Beer

class BeerFixture implements TemplateLoader {

    static final String WITHOUT_BREWERY = 'without-brewery'

    @Override
    void load() {

        Fixture.of(Beer)
                .addTemplate(WITHOUT_BREWERY, new Rule() {
                    {
                        add('id', random(Long, range(1L, 99999L)))
                        add('name', 'Gordelicia')
                        add('style', 'Blonde Ale')
                        add('abv', 8.5f)
                    }
                })
    }
}
