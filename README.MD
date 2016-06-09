 How to use from commandline

    Parameters:

        mode

        It defines the process(es) to be executed. Possible values are:
            morphparse (segmentation and POS-tagging)
            depparse (segmentation, POS-tagging and dependency parsing)
            constparse (segmentation, POS-tagging and constituency parsing)
            morana (possible morphological analyses of a given word)
            gui (graphical user interface)

        input

        It defines the input file on which the process will be executed. The input file must be a txt file containing running (raw) text.

        output

        It defines the output file in which the analysis will be saved.
            In the case of morphparse, the output file has the following structure. One line corresponds to one token and sentences are separated by an empty line. The first column contains the wordform, the second one contains the lemma and the third one contains the MSD code.
            In the case of depparse, the output file has the following structure. One line corresponds to one token and sentences are separated by an empty line. The first column contains the identifier of the word within the sentence, the second column contains the wordform, the third one the lemma, the fourth one the MSD code, the fifth one the part of speech, the sixth one the morphological features, the seventh one the identifier of the parent node, and finally the eighth one contains the dependency label.
            In the case of constparse, the output file has the following structure. One line corresponds to one token and sentences are separated by an empty line. The first column contains the identifier of the word within the sentence, the second column contains the wordform, the third one the lemma, the fourth one the MSD code, the fifth one the part of speech, the sixth one the morphological features, and the seventh one contains the syntactic label.

        encoding

        This is an optional parameter, with which the character encoding of the input and output files can be defined. By default, UTF-8 is used.

        spelling

        In the case of morana, this defines the word to be analyzed by the morphological analyzer.
    Examples:

        java -Xmx1G -jar magyarlanc-3.0.jar -mode morphparse -input in.txt -output out.txt

        java -Xmx2G -jar magyarlanc-3.0.jar -mode depparse -input in.txt -output out.txt -encoding ISO-8859-2

        java -Xmx2G -jar magyarlanc-3.0.jar -mode gui

        java -Xmx2G -jar magyarlanc-3.0.jar -mode morana -spelling almáknak

# magyarlanc
