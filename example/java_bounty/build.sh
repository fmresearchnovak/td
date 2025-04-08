#!/bin/bash


mkdir -p build
cd build
cmake -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_PREFIX:PATH=./ -DTd_DIR:PATH=$(readlink -e ../td/lib/cmake/Td) ..
cmake --build . --target install

cd ../
echo -e "#!/bin/bash\ncd build/bin/\njava -Djava.library.path=./ edu.fandm.enovak.ParcelLogin" > run.sh
chmod +x run.sh

echo "To run from the build/bin/ directory:"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelLogin"
echo "Or execute ./run.sh"