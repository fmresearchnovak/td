#!/bin/bash


mkdir -p build
cd build
cmake -DCMAKE_BUILD_TYPE=Debug -DCMAKE_INSTALL_PREFIX:PATH=./ -DTd_DIR:PATH=$(readlink -e ../td/lib/cmake/Td) ..
cmake --build . --target install

cd ../

echo -e "#!/bin/bash\n\n# This file was automatically generated from build.sh\n\ncd build/bin/\njava -Djava.library.path=./ edu.fandm.enovak.ParcelSendMessage \$1 \"\$2\"" > run-send-message.sh
chmod +x run-send-message.sh

echo -e "#!/bin/bash\n\n# This file was automatically generated from build.sh\n\ncd build/bin/\njava -Djava.library.path=./ edu.fandm.enovak.ParcelProbeImportContacts \$1" > run-probe-import-contacts.sh
chmod +x run-probe-import-contacts.sh

echo -e "#!/bin/bash\n\n#This file was automatically generated from build.sh\n\n# convert second parameter from relative file path to absolute file path\nif [ -n \"$2\" ]; then\n\tabs_path=$(readlink -e \"$2\")\nelse\n\tabs_path=\"\"\nfi\n\ncd build/bin/\njava -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage $1 $abs_path"
chmod +x run-send-vcard-message.sh

echo "To run from the build/bin/ directory:"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelSendMessage <number> <msg>"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelProbeImportContacts <number>"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage <number> <path/to/card.vcf>"
echo "OR use the provided run scripts..."
echo "  ./run-send-message.sh <number> <msg>"
echo "  ./run-probe-import-contacts.sh <number>"
echo "  ./run-send-vcard-message.sh <number> <path/to/card.vcf>"