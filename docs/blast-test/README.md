# Blast Tests

These tests were done to check whether blastn outputs the GI in a particular field

**Input**

These 4 reads

```bash
>read1
GCTTTGGATACTGTAGGTCTTGAGTGCAGGAGAGGTAAGTGGAATTCCTAGTGTAGCGG
TGAAATGCGTAGATATTAGGAGGAACACCAGTGGCGAAGGCGGCTTACTGGACTGTAAC
TGACGTTGAGGCTC
>read2
CCGTTTGATCCTTTGTCTCTTAGATGCGACGAGGGAGGCGGGACATGTTTAGTTGGGGG
GAAATTTATTGATTTTTCTTGGAACCCCCATTTCGGAGGGAGCCTTCCTGGCCTGTTGT
GGACGTTGATGCTG
>read3
GCATCCAATACTGTCGGACTTGAGTGCAGGAGAGGAAAGCGGAATTCCTAGTGTAGCGG
TGAAATGCGTAGATATTAGGAGGAACACCGGTGGCGAAGGCGGCTTTCTGGACTGTAAC
TGACGCTGAGGCAC
>read4
GCTTTGGAAACTGTGCAGCTAGAGTGTCGGAGAGGTAAGTGGAATTCCTAGTGTAGCGG
TGAAATGCGTAGATATTAGGAGGAACACCAGTGGCGAAGACGGCTTACTGGACGATGAC
TGACGTTGAGGCTG
```

**Instructions**

Two tests were run to see whether the `-show_gis` option was needed. Conclusions

- `-show_gis` option is not needed to output the subject GI in an independent field

- Setting the `-outfmt` to 7 and specifying `sgi` we have the subject GI in a independent field in the output file

Test 1

```bash
./ncbi-blast-2.2.31+/bin/blastn -db era7.16S.reference.sequences.0.1.0.fasta -query reads.fasta -out blastn.test.out.txt -outfmt "7 qseqid qlen sseqid sgi sacc slen qstart qend sstart send evalue" -show_gis  -num_alignments 10
```

Test 2

```bash
./ncbi-blast-2.2.31+/bin/blastn -db era7.16S.reference.sequences.0.1.0.fasta -query reads.fasta -out blastn.test2.out.txt -outfmt "7 qseqid qlen sseqid sgi sacc slen qstart qend sstart send evalue"  -num_alignments 10
```

Test 3

```bash
./ncbi-blast-2.2.31+/bin/blastn -db era7.16S.reference.sequences.0.1.0.fasta -query reads.fasta -out blastn.test3.out.txt -outfmt "10 qseqid qlen sseqid sgi sacc slen qstart qend sstart send evalue"  -num_alignments 10
```



**Output**

- `s3://era7p/metagenomica/data/out/blast-test/blastn.test.out.txt`
- `s3://era7p/metagenomica/data/out/blast-test/blastn.test2.out.txt`
- `s3://era7p/metagenomica/data/out/blast-test/blastn.test3.out.txt`