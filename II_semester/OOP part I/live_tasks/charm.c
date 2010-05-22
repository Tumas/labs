#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LEN 256

/*  variantas #2 */

void printMatrix(char *pmat, int rowLen)
{
    int j, i;
    for (i = 0; i < rowLen; i++){
        for (j = 0; j < rowLen; j++)
            printf("%c ", *(pmat + i*rowLen + j));
        printf("\n");
    }
}

void populateMatrix(char *pmat, int rowLen, int rowNum, char *string)
{
    //populate row
    int rowIndex = rowNum;
    int colIndex;
    int strIndex = 0;

    for (colIndex = 0; colIndex < rowLen; colIndex++)
        if ((*(pmat + rowLen * rowIndex + colIndex)) == 0){
            *(pmat + rowLen * rowIndex + colIndex) = *(string + strIndex);
            strIndex++;
        }
    //populate diagonal
    rowIndex = rowLen - 1;
    colIndex = rowLen - rowNum - 1;
    for (; rowIndex > 0 && colIndex < rowLen; rowIndex--, colIndex++)
        if ((*(pmat + rowLen * rowIndex + colIndex)) == 0){
            *(pmat + rowLen * rowIndex + colIndex) = *(string + strIndex);
            strIndex++;
    }

    //printMatrix(pmat, rowLen);
    if (rowNum != rowLen)
        populateMatrix(pmat, rowLen, rowNum + 1, string + strIndex);

    return;
}

int main()
{
    char eil[MAX_LEN];

    printf("Iveskite simboliu eilute\n");   
    fgets(eil, MAX_LEN, stdin);

    int len = (int) pow(strlen(eil), 0.5);
    //interpretuosim kaip 2D nors is tikro tai vienas ilgas vienmatis
    //butu gerai, kad masyvas butu iskarto apnulintas
    char *pmat = (char*) calloc(len*len, sizeof(char)); 
    populateMatrix(pmat, len, 0, eil);
    printMatrix(pmat, len); 

    free(pmat);
    return(0);
}