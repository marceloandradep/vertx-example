package com.pismo.vertxexample.fixtures

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader

class FixtureLoader {

    static void loadTemplates() {
        FixtureFactoryLoader.loadTemplates(FixtureLoader.class.getPackage().getName())
    }
}
