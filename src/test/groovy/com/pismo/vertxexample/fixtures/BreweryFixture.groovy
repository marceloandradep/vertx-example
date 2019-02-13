package com.pismo.vertxexample.fixtures

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader
import com.pismo.vertxexample.domain.Brewery

class BreweryFixture implements TemplateLoader {

    static final String DEFAULT = 'default'

    @Override
    void load() {

        Fixture.of(Brewery)
                .addTemplate(DEFAULT, new Rule() {
                    {
                        add('id', random(Long, range(1L, 99999L)))
                        add('name', 'Urbana')
                        add('location', 'Jabaquara, SP')
                    }
                })
    }
}
