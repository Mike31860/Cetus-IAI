#include <stdio.h>

#include <stdlib.h>

#include <string.h>

#include <errno.h>

#include <ctype.h>

#include <math.h>

#include "adc.h"

#include "macrodef.h"

#include "npbparams.h"

#include "../common/wtime.c"

#include "../common/c_timers.c"

#include "../common/c_print_results.c"

#include "../common/type.h"

#include <sys/types.h>

#include <unistd.h>

void timer_clear(int );
void timer_start(int );
void timer_stop(int );
double timer_read(int );
void c_print_results(char * name, char clss, int n1, int n2, int n3, int niter, double t, double mops, char * optype, int passed_verification, char * npbversion, char * compiletime, char * cc, char * clink, char * c_lib, char * c_inc, char * cflags, char * clinkflags);
void initADCpar(ADC_PAR * par);
int ParseParFile(char * parfname, ADC_PAR * par);
int GenerateADC(ADC_PAR * par);
void ShowADCPar(ADC_PAR * par);
int32 DC(ADC_VIEW_PARS * adcpp);
int Verify(long long int checksum, ADC_VIEW_PARS * adcpp);
int main(int argc, char * argv[])
{
	ADC_PAR * parp;
	ADC_VIEW_PARS * adcpp;
	int32 retCode;
	int _ret_val_0;
	fprintf( & _iob[1], "\n\n NAS Parallel Benchmarks (NPB3.3-SER) - DC Benchmark\n\n");
	if (argc!=3)
	{
		fprintf( & _iob[1], " No Paramter file. Using compiled defaults\n");
	}
	if ((argc>3)||((argc>1)&&( ! isdigit(argv[1][0]))))
	{
		fprintf( & _iob[2], "Usage: <program name> <amount of memory>\n");
		fprintf( & _iob[2], "       <file of parameters>\n");
		fprintf( & _iob[2], "Example: bin/dc.S 1000000 DC/ADC.par\n");
		fprintf( & _iob[2], "The last argument, (a parameter file) can be skipped\n");
		exit(1);
	}
	if (( ! (parp=((ADC_PAR * )malloc(sizeof (ADC_PAR)))))||( ! (adcpp=((ADC_VIEW_PARS * )malloc(sizeof (ADC_VIEW_PARS))))))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "main: malloc failed",  * _errno());
		exit(1);
	}
	initADCpar(parp);
	parp->clss='B';
	if (argc!=3)
	{
		parp->dim=attrnum;
		parp->tuplenum=input_tuples;
	}
	else
	{
		if ((argc==3)&&( ! ParseParFile(argv[2], parp)))
		{
			fprintf( & _iob[2], " %s, errno = %d\n", "main.ParseParFile failed",  * _errno());
			exit(1);
		}
	}
	ShowADCPar(parp);
	if ( ! GenerateADC(parp))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "main.GenerateAdc failed",  * _errno());
		exit(1);
	}
	adcpp->ndid=parp->ndid;
	adcpp->clss=parp->clss;
	adcpp->nd=parp->dim;
	adcpp->nm=parp->mnum;
	adcpp->nTasks=1;
	if (argc>=2)
	{
		adcpp->memoryLimit=atoi(argv[1]);
	}
	else
	{
		adcpp->memoryLimit=0;
	}
	if (adcpp->memoryLimit<=0)
	{
		adcpp->memoryLimit=(parp->tuplenum*(50+(5*parp->dim)));
		fprintf( & _iob[1], "Estimated rb-tree size = %d \n", adcpp->memoryLimit);
	}
	adcpp->nInputRecs=parp->tuplenum;
	strcpy(adcpp->adcName, parp->filename);
	strcpy(adcpp->adcInpFileName, parp->filename);
	if (retCode=DC(adcpp))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "main.DC failed",  * _errno());
		fprintf( & _iob[2], "main.ParRun failed: retcode = %d\n", retCode);
		exit(1);
	}
	if (parp)
	{
		free(parp);
		parp=0;
	}
	if (adcpp)
	{
		free(adcpp);
		adcpp=0;
	}
	_ret_val_0=0;
	return _ret_val_0;
}

int32 CloseAdcView(ADC_VIEW_CNTL * adccntl);
int32 PartitionCube(ADC_VIEW_CNTL * avp);
ADC_VIEW_CNTL *NewAdcViewCntl(ADC_VIEW_PARS * adcpp, uint32 pnum);
int32 ComputeGivenGroupbys(ADC_VIEW_CNTL * adccntl);
int32 DC(ADC_VIEW_PARS * adcpp)
{
	int32 itsk = 0;
	double t_total = 0.0;
	int verified;
	struct named_dc_c_29121
	{
		int verificationFailed;
		uint32 totalViewTuples;
		uint64 totalViewSizesInBytes;
		uint32 totalNumberOfMadeViews;
		uint64 checksum;
		double tm_max;
	};
	;
	typedef struct named_dc_c_29121 PAR_VIEW_ST;
	PAR_VIEW_ST * pvstp;
	ADC_VIEW_CNTL * adccntlp;
	int32 _ret_val_0;
	pvstp=((PAR_VIEW_ST * )malloc(sizeof (PAR_VIEW_ST)));
	pvstp->verificationFailed=0;
	pvstp->totalViewTuples=0;
	pvstp->totalViewSizesInBytes=0;
	pvstp->totalNumberOfMadeViews=0;
	pvstp->checksum=0;
	adccntlp=NewAdcViewCntl(adcpp, itsk);
	if ( ! adccntlp)
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "ParRun.NewAdcViewCntl: returned NULL",  * _errno());
		_ret_val_0=2;
		return _ret_val_0;
	}
	else
	{
		if (adccntlp->retCode!=0)
		{
			fprintf( & _iob[2], "DC.NewAdcViewCntl: return code = %d\n", adccntlp->retCode);
		}
	}
	if (PartitionCube(adccntlp))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "DC.PartitionCube failed",  * _errno());
		;
	}
	timer_clear(itsk);
	timer_start(itsk);
	if (ComputeGivenGroupbys(adccntlp))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "DC.ComputeGivenGroupbys failed",  * _errno());
		;
	}
	timer_stop(itsk);
	pvstp->tm_max=timer_read(itsk);
	pvstp->verificationFailed+=adccntlp->verificationFailed;
	if ( ! adccntlp->verificationFailed)
	{
		pvstp->totalNumberOfMadeViews+=adccntlp->numberOfMadeViews;
		pvstp->totalViewSizesInBytes+=adccntlp->totalViewFileSize;
		pvstp->totalViewTuples+=adccntlp->totalOfViewRows;
		pvstp->checksum+=adccntlp->totchs[0];
	}
	if (CloseAdcView(adccntlp))
	{
		fprintf( & _iob[2], " %s, errno = %d\n", "ParRun.CloseAdcView: is failed",  * _errno());
		;
		adccntlp->verificationFailed=1;
	}
	t_total=pvstp->tm_max;
	pvstp->verificationFailed=Verify(pvstp->checksum, adcpp);
	verified=((pvstp->verificationFailed==( - 1)) ? ( - 1) : ((pvstp->verificationFailed==0) ? 1 : 0));
	fprintf( & _iob[1], "\n*** DC Benchmark Results:\n");
	fprintf( & _iob[1], " Benchmark Time   = %20.3f\n", t_total);
	fprintf( & _iob[1], " Input Tuples     =         %12d\n", (int)adcpp->nInputRecs);
	fprintf( & _iob[1], " Number of Views  =         %12d\n", (int)pvstp->totalNumberOfMadeViews);
	fprintf( & _iob[1], " Number of Tasks  =         %12d\n", (int)adcpp->nTasks);
	fprintf( & _iob[1], " Tuples Generated = %20.0f\n", (double)pvstp->totalViewTuples);
	fprintf( & _iob[1], " Tuples/s         = %20.2f\n", ((double)pvstp->totalViewTuples)/t_total);
	fprintf( & _iob[1], " Checksum         = %20.12e\n", (double)pvstp->checksum);
	if (pvstp->verificationFailed)
	{
		fprintf( & _iob[1], " Verification failed\n");
	}
	c_print_results("DC", adcpp->clss, (int)adcpp->nInputRecs, 0, 0, 1, t_total, (((double)pvstp->totalViewTuples)*1.0E-6)/t_total, "Tuples generated", verified, "3.3.1", "15 Dec 2021", "gcc", "$(CC)", "-lm", "-I../common", "-g -Wall -O3 -fopenmp -mcmodel=large", "-O3 -fopenmp -mcmodel=large");
	_ret_val_0=0;
	return _ret_val_0;
}

long long checksumS = 464620213;
long long checksumWlo = 434318;
long long checksumWhi = 1401796;
long long checksumAlo = 178042;
long long checksumAhi = 7141688;
long long checksumBlo = 700453;
long long checksumBhi = 9348365;
int Verify(long long int checksum, ADC_VIEW_PARS * adcpp)
{
	int _ret_val_0;
	switch (adcpp->clss)
	{
		case 'S':
		if (checksum==checksumS)
		{
			_ret_val_0=0;
			return _ret_val_0;
		}
		break;
		case 'W':
		if (checksum==(checksumWlo+(1000000*checksumWhi)))
		{
			_ret_val_0=0;
			return _ret_val_0;
		}
		break;
		case 'A':
		if (checksum==(checksumAlo+(1000000*checksumAhi)))
		{
			_ret_val_0=0;
			return _ret_val_0;
		}
		break;
		case 'B':
		if (checksum==(checksumBlo+(1000000*checksumBhi)))
		{
			_ret_val_0=0;
			return _ret_val_0;
		}
		break;
		default:
		_ret_val_0=( - 1);
		return _ret_val_0;
	}
	_ret_val_0=1;
	return _ret_val_0;
}
