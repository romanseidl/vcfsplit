# VCF Split

This little Java program splits a VCF file consisting of multiple vcards into single VCF files. This is particualry useful if you want to import a vcf into Microsoft Outlook.

It ist a executable jar file, so starting it with 

    java -jar vcfsplitter.jar  

or _double clicking_ it in a gui should work fine. 

You can also specify evertyhing by command line as follows:

    java -jar vcfsplitter.jar  <infile> <outdir>
    
(c) 2016 by Roman Seidl
