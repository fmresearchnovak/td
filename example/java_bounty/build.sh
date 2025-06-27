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

cat << 'EOF' > "run-send-vcard-message.sh"
#!/bin/bash

#This file was automatically generated from build.sh

# convert second parameter from relative file path to absolute file path
if [ -n "$2" ]; then
	ABS_PATH=$(readlink -f "$2" 2>/dev/null)
else
    echo "Missing required command line parameters"
    exit 1
fi

cd build/bin/
java -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage $1 "$ABS_PATH" 

EOF

chmod +x run-send-vcard-message.sh

echo -e "\n\nTo run from the build/bin/ directory:"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelSendMessage <number> <msg>"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelProbeImportContacts <number>"
echo "java -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage <number> <path/to/card.vcf>"
echo -e "\nOR use the provided run scripts..."
echo "  ./run-send-message.sh <number> <msg>"
echo "  ./run-probe-import-contacts.sh <number>"
echo "  ./run-send-vcard-message.sh <number> <path/to/card.vcf>"