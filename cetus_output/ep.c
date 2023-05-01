#include <stdlib.h>
#include <stdio.h>

#include <stdlib.h>

#include <math.h>

#include "type.h"

#include "npbparams.h"

#include "randdp.h"

#include "timers.h"

#include <stdio.h>

#include <time.h>

#include "print_results.h"

static double x[(2*(1<<16))];
static double q[10];
double time_spent = 0.0;
double time_spent2 = 0.0;
double time_spent3 = 0.0;
double time_spent4 = 0.0;
int main()
{
	double Mops, t1, t2, t3, t4, x1, x2;
	double sx, sy, tm, an, tt, gc;
	double sx_verify_value, sy_verify_value, sx_err, sy_err;
	int np;
	int i, ik, kk, l, k, nit;
	int k_offset, j;
	logical verified, timers_enabled;
	double dum[3];
	char size[16];
	FILE * fp;
	clock_t begin = clock();
	clock_t end = clock();
	clock_t begin2 = clock();
	clock_t end2 = clock();
	clock_t begin3 = clock();
	clock_t end3 = clock();
	clock_t begin4 = clock();
	clock_t end4 = clock();
	int _ret_val_0;
	if ((fp=fopen("timer.flag", "r"))==((void * )0))
	{
		timers_enabled=false;
	}
	else
	{
		timers_enabled=true;
		fclose(fp);
	}
	sprintf(size, "%15.0lf", pow(2.0, 32+1));
	j=14;
	if (size[j]=='.')
	{
		j -- ;
	}
	size[j+1]='\0';
	printf("\n\n NAS Parallel Benchmarks (NPB3.3-SER-C) - EP Benchmark\n");
	printf("\n Number of random numbers generated: %15s\n", size);
	verified=false;
	np=(1<<(32-16));
	vranlc(0,  & dum[0], dum[1],  & dum[2]);
	dum[0]=randlc( & dum[1], dum[2]);
	#pragma cetus private(i) 
	#pragma loop name main#0 
	#pragma cetus parallel 
	#pragma omp parallel for private(i)
	for (i=0; i<(2*(1<<16)); i ++ )
	{
		x[i]=( - 1.0E99);
	}
	Mops=log(sqrt(fabs(((1.0>1.0) ? 1.0 : 1.0))));
	timer_clear(0);
	timer_clear(1);
	timer_clear(2);
	timer_start(0);
	t1=1.220703125E9;
	vranlc(0,  & t1, 1.220703125E9, x);
	t1=1.220703125E9;
	#pragma cetus private(i, t2) 
	#pragma loop name main#1 
	for (i=0; i<(16+1); i ++ )
	{
		t2=randlc( & t1, t1);
	}
	time_spent+=(((double)(end-begin))/((clock_t)1000));
	an=t1;
	tt=2.71828183E8;
	gc=0.0;
	sx=0.0;
	sy=0.0;
	#pragma cetus private(i) 
	#pragma loop name main#2 
	#pragma cetus parallel 
	/*
	Disabled due to low profitability: #pragma omp parallel for private(i)
	*/
	for (i=0; i<10; i ++ )
	{
		q[i]=0.0;
	}
	time_spent2+=(((double)(end2-begin2))/((clock_t)1000));
	k_offset=( - 1);
	#pragma cetus private(i, ik, k, kk, l, t3, t4, x1, x2) 
	#pragma loop name main#3 
	/* #pragma cetus reduction(+: q[l], sx, sy)  */
	for (k=1; k<=np; k ++ )
	{
		kk=(k_offset+k);
		t1=2.71828183E8;
		t2=an;
		#pragma loop name main#3#0 
		for (i=1; i<=100; i ++ )
		{
			ik=(kk/2);
			if ((2*ik)!=kk)
			{
				t3=randlc( & t1, t2);
			}
			if (ik==0)
			{
				break;
			}
			t3=randlc( & t2, t2);
			kk=ik;
		}
		if (timers_enabled)
		{
			timer_start(2);
		}
		vranlc(2*(1<<16),  & t1, 1.220703125E9, x);
		if (timers_enabled)
		{
			timer_stop(2);
		}
		if (timers_enabled)
		{
			timer_start(1);
		}
		#pragma cetus parallel 
		#pragma cetus private(i, l, t1, t2, t3, t4, x1, x2) 
		#pragma omp parallel private(i, l, t1, t2, t3, t4, x1, x2)
		{
			double * reduce = (double * )malloc(10*sizeof (double));
			int reduce_span_0;
			for (reduce_span_0=0; reduce_span_0<10; reduce_span_0 ++ )
			{
				reduce[reduce_span_0]=0;
			}
			#pragma loop name main#3#1 
			#pragma cetus reduction(+: sx, sy) 
			#pragma cetus for  
			#pragma omp for reduction(+: sx, sy)
			for (i=0; i<(1<<16); i ++ )
			{
				x1=((2.0*x[2*i])-1.0);
				x2=((2.0*x[(2*i)+1])-1.0);
				t1=((x1*x1)+(x2*x2));
				if (t1<=1.0)
				{
					t2=sqrt((( - 2.0)*log(t1))/t1);
					t3=(x1*t2);
					t4=(x2*t2);
					l=((fabs(t3)>fabs(t4)) ? fabs(t3) : fabs(t4));
					reduce[l]=(reduce[l]+1.0);
					sx=(sx+t3);
					sy=(sy+t4);
				}
			}
			#pragma cetus critical  
			#pragma omp critical
			{
				for (reduce_span_0=0; reduce_span_0<10; reduce_span_0 ++ )
				{
					q[reduce_span_0]+=reduce[reduce_span_0];
				}
			}
		}
		if (timers_enabled)
		{
			timer_stop(1);
		}
	}
	time_spent3+=(((double)(end3-begin3))/((clock_t)1000));
	#pragma cetus private(i) 
	#pragma loop name main#4 
	#pragma cetus reduction(+: gc) 
	#pragma cetus parallel 
	/*
	Disabled due to low profitability: #pragma omp parallel for private(i) reduction(+: gc)
	*/
	for (i=0; i<10; i ++ )
	{
		gc=(gc+q[i]);
	}
	time_spent4+=(((double)(end4-begin4))/((clock_t)1000));
	timer_stop(0);
	tm=timer_read(0);
	nit=0;
	verified=true;
	sx_verify_value=47643.67927995374;
	sy_verify_value=( - 80840.72988043731);
	if (verified)
	{
		sx_err=fabs((sx-sx_verify_value)/sx_verify_value);
		sy_err=fabs((sy-sy_verify_value)/sy_verify_value);
		verified=((sx_err<=1.0E-8)&&(sy_err<=1.0E-8));
	}
	Mops=((pow(2.0, 32+1)/tm)/1000000.0);
	printf("\nEP Benchmark Results:\n\n");
	printf("CPU Time =%10.4lf\n", tm);
	printf("N = 2^%5d\n", 32);
	printf("No. Gaussian Pairs = %15.0lf\n", gc);
	printf("Sums = %25.15lE %25.15lE\n", sx, sy);
	printf("Counts: \n");
	#pragma cetus private(i) 
	#pragma loop name main#5 
	for (i=0; i<10; i ++ )
	{
		printf("%3d%15.0lf\n", i, q[i]);
	}
	print_results("EP", 'C', 32+1, 0, 0, nit, tm, Mops, "Random numbers generated", verified, "3.3.1", "02 Aug 2021", "gcc", "$(CC)", "-lm", "-I../common", "-g -Wall -O3 -fopenmp -mcmodel=large", "-O3 -fopenmp -mcmodel=large", "randdp");
	if (timers_enabled)
	{
		if (tm<=0.0)
		{
			tm=1.0;
		}
		tt=timer_read(0);
		printf("\nTotal time:     %9.3lf (%6.2lf)\n", tt, (tt*100.0)/tm);
		tt=timer_read(1);
		printf("Gaussian pairs: %9.3lf (%6.2lf)\n", tt, (tt*100.0)/tm);
		tt=timer_read(2);
		printf("Random numbers: %9.3lf (%6.2lf)\n", tt, (tt*100.0)/tm);
	}
	printf("The elapsed time 1 is %f seconds \n", time_spent);
	printf("The elapsed time 2 is %f seconds \n", time_spent2);
	printf("The elapsed time 3 is %f seconds \n", time_spent3);
	printf("The elapsed time 4 is %f seconds \n", time_spent4);
	_ret_val_0=0;
	return _ret_val_0;
}
