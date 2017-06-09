InstAL Installation Instructions
=======================================

Requirements
------------

Python (3.X - 3.4 advised.)

Python-pip

Scons (to build clingo)

g++ 4.9.0 or upwards.

Clingo (from clingo-5.1.0 source https://github.com/potassco/clingo/releases/tag/v5.1.0 )

Virtualenv

Virtualenv setup
---------------------

Virtual environments (virtualenvs) allow you to better manage your python dependencies by creating a distinct python environment for each application you're using.
To install virtualenv, use ```sudo pip install virtualenv virtualenvwrapper```

To create a virtualenv, use ```mkvirtualenv instal -p python3.4```. You'll see that your bash prompt should change to have (instal) in front of it.

To turn off the virtualenv, use ```deactivate```.

To change to the virtualenv, use ```workon instal```.

You can now use the pip install instructions in the next section.

Python requirements - pip
---------------------------------

To install all the python requirements, use  ```sudo pip3 install -r requirements.txt```

Compiling Clingo from source
----------------------------

Download the Clingo source from https://github.com/potassco/clingo/releases/tag/v5.1.0

Decompress the download and cd into the directory.

Edit build/release.py to include:
CXX = {your version of g++}
CC = {your version of GCC}
PYTHON_CONFIG = "python3-config"

Run ```scons --build-dir=release```

Copy build/release/python/clingo.so into instal/instal/
