package com.asdev.libjam.md.xml

import java.io.File

/**
 * Created by Asdev on 03/03/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.xml
 */

/**
 * Transpiles the input xml value files into Kotlin objects.
 */
class GenTranspiler {

    fun main(args: Array<String>) {
        // get the parameters
        var inputFolder: String? = null
        var outputFolder: String? = null

        // process the paramters
        for(i in 0 until args.size) {
            if(args[i] == "--help") {
                println("Usage: gen-transpiler --input [input folder] --output [output folder]")
                return
            } else if(args[i] == "--input") {
                if(i + 1 >= args.size) {
                    println("Input not specified!")
                    return
                }
                // the next arg will be the input folder
                inputFolder = args[i + 1]
            }  else if(args[i] == "--output") {
                if (i + 1 >= args.size) {
                    println("Output not specified!")
                    return
                }
                // the next arg will be the input folder
                outputFolder = args[i + 1]
            }
        }

        if(inputFolder == null) {
            println("No input specified")
            return
        } else if(outputFolder == null) {
            println("No output specified")
            return
        }

        val inputDir = File(inputFolder)
        val outputDir = File(outputFolder)
        run(inputDir, outputDir)
    }

    fun run(inputDir: File, outputDir: File) {
        // get all directories and files in this dir that end with xml or are directories
        val files = inputDir.listFiles { f: File, s: String -> f.isDirectory || s.endsWith(".xml", true) }

        for(file in files) {
            if(file.isDirectory) {
                run(file, outputDir)
            } else {
                // read the contents of the file

                // determine the type of file it is

                // send it for parsing
            }
        }
    }

}