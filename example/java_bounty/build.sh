#!/bin/bash

cd ../../
#rm -rf build/
mkdir -p build
cd build
cmake -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_PREFIX:PATH=../example/java_bounty/td -DTD_ENABLE_JNI=ON ..
cmake --build . --target install

# ask user to press enter to continue
#read -p "Press Enter to continue..."

cd ../
cd example/java_bounty
#rm -rf build
mkdir -p build
cd build
cmake -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_PREFIX:PATH=./ -DTd_DIR:PATH=$(readlink -e ../td/lib/cmake/Td) ..
cmake --build . --target install

echo "To run from the build/bin/ directory:"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelLogin"