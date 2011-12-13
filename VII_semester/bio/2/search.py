#!/usr/bin/python

from Bio.Blast import NCBIWWW, NCBIXML

import sys

def protein_blast(protein, criteria, threshold, filename = 'blast.fasta', db = 'swissprot'):
    """ perform blast search + filter by percentage coverage """
    handle = NCBIWWW.qblast('blastp', db, protein, entrez_query = criteria)
    result = NCBIXML.read(handle)

    out = open(filename, 'w')
    for alignment in result.alignments:
        sequence = alignment.hsps[0]

        if ((float)(sequence.positives) / sequence.align_length * 100.0) >= threshold:
            out.write('>' + alignment.hit_id + '\n' + sequence.sbjct + '\n\n')

    out.close

def load_sequences(filename = 'blast.fasta', s = []):
    """ load syntaxes from fasta file into an array """
    with open(filename, 'r') as f:
        fasta_line = False

        for line in f:
            if fasta_line:
                s.append(line.rstrip())   

            fasta_line = line[0] == '>'

    return s
    
    
def subsequence(other, fragment_length = 15, minimum = False):
    """ 
      find best/worst sebsequence (BF) 
    """
    sequence = other[0]
    range_seq = range(0, len(other))
    threshold = sys.maxint if minimum else 0

    # for each fragment
    selected = 0 
    for start in range(len(sequence) - fragment_length + 1):

        # with each sequence
        # i, j -> sequence index
        frag_match = 0
        end = start + fragment_length
        for i in range_seq:
            for j in range_seq[i:]:
                if j != i:
                    delta = match_score(other[i][start:end], other[j][start:end])  
                    frag_match += delta

        if (minimum and frag_match < threshold) or (not minimum and frag_match > threshold):
            threshold = frag_match 
            selected = start

    return sequence[selected:selected + fragment_length] 

def match_score(s1, s2):
    """ align two sequences and count matching score """
    return sum(s1[i] == s2[i] for i in range(min(len(s1), len(s2))))

# task 2
#protein_blast('NP_000468.1', '"serum albumin"[Protein name] AND mammals[Organism]', 80)

# task 3 
sequences = load_sequences()
min_sub = subsequence(sequences, minimum = True)
print min_sub

max_sub = subsequence(sequences)
print max_sub
