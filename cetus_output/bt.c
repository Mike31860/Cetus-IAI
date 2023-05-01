#include <stdio.h>

#include <stdlib.h>

#include "header.h"

#include "timers.h"

#include "../common/print_results.h"

double elapsed_time;
int grid_points[3];
logical timeron;
double tx1, tx2, tx3, ty1, ty2, ty3, tz1, tz2, tz3, dx1, dx2, dx3, dx4, dx5, dy1, dy2, dy3, dy4, dy5, dz1, dz2, dz3, dz4, dz5, dssp, dt, ce[5][13], dxmax, dymax, dzmax, xxcon1, xxcon2, xxcon3, xxcon4, xxcon5, dx1tx1, dx2tx1, dx3tx1, dx4tx1, dx5tx1, yycon1, yycon2, yycon3, yycon4, yycon5, dy1ty1, dy2ty1, dy3ty1, dy4ty1, dy5ty1, zzcon1, zzcon2, zzcon3, zzcon4, zzcon5, dz1tz1, dz2tz1, dz3tz1, dz4tz1, dz5tz1, dnxm1, dnym1, dnzm1, c1c2, c1c5, c3c4, c1345, conz1, c1, c2, c3, c4, c5, c4dssp, c5dssp, dtdssp, dttx1, dttx2, dtty1, dtty2, dttz1, dttz2, c2dttx1, c2dtty1, c2dttz1, comz1, comz4, comz5, comz6, c3c4tx3, c3c4ty3, c3c4tz3, c2iv, con43, con16;
double us[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double vs[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double ws[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double qs[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double rho_i[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double square[64][(((64/2)*2)+1)][(((64/2)*2)+1)];
double forcing[64][(((64/2)*2)+1)][(((64/2)*2)+1)][5];
double u[64][(((64/2)*2)+1)][(((64/2)*2)+1)][5];
double rhs[64][(((64/2)*2)+1)][(((64/2)*2)+1)][5];
double cuf[(64+1)];
double q[(64+1)];
double ue[(64+1)][5];
double buf[(64+1)][5];
double fjac[(64+1)][5][5];
double njac[(64+1)][5][5];
double lhs[(64+1)][3][5][5];
double tmp1, tmp2, tmp3;
int main(int argc, char * argv[])
{
	int i, niter, step;
	double navg, mflops, n3;
	double tmax, t, trecs[(11+1)];
	logical verified;
	char Class;
	char * t_names[(11+1)];
	FILE * fp;
	int _ret_val_0;
	if ((fp=fopen("timer.flag", "r"))!=((void * )0))
	{
		timeron=true;
		t_names[1]="total";
		t_names[2]="rhsx";
		t_names[3]="rhsy";
		t_names[4]="rhsz";
		t_names[5]="rhs";
		t_names[6]="xsolve";
		t_names[7]="ysolve";
		t_names[8]="zsolve";
		t_names[9]="redist1";
		t_names[10]="redist2";
		t_names[11]="add";
		fclose(fp);
	}
	else
	{
		timeron=false;
	}
	printf("\n\n NAS Parallel Benchmarks (NPB3.3-SER-C) - BT Benchmark\n\n");
	if ((fp=fopen("inputbt.data", "r"))!=((void * )0))
	{
		int result;
		printf(" Reading from input file inputbt.data\n");
		result=fscanf(fp, "%d",  & niter);
		while (fgetc(fp)!='\n')
		{
			;
		}
		result=fscanf(fp, "%lf",  & dt);
		while (fgetc(fp)!='\n')
		{
			;
		}
		result=fscanf(fp, "%d%d%d\n",  & grid_points[0],  & grid_points[1],  & grid_points[2]);
		fclose(fp);
	}
	else
	{
		printf(" No input file inputbt.data. Using compiled defaults\n");
		niter=200;
		dt=8.0E-4;
		grid_points[0]=64;
		grid_points[1]=64;
		grid_points[2]=64;
	}
	printf(" Size: %4dx%4dx%4d\n", grid_points[0], grid_points[1], grid_points[2]);
	printf(" Iterations: %4d    dt: %10.6f\n", niter, dt);
	printf("\n");
	if (((grid_points[0]>64)||(grid_points[1]>64))||(grid_points[2]>64))
	{
		printf(" %d, %d, %d\n", grid_points[0], grid_points[1], grid_points[2]);
		printf(" Problem size too big for compiled array sizes\n");
		_ret_val_0=0;
		return _ret_val_0;
	}
	set_constants();
	#pragma cetus private(i) 
	#pragma loop name main#0 
	for (i=1; i<=11; i ++ )
	{
		timer_clear(i);
	}
	initialize();
	exact_rhs();
	adi();
	initialize();
	#pragma cetus private(i) 
	#pragma loop name main#1 
	for (i=1; i<=11; i ++ )
	{
		timer_clear(i);
	}
	timer_start(1);
	#pragma cetus private(step) 
	#pragma loop name main#2 
	for (step=1; step<=niter; step ++ )
	{
		if (((step%20)==0)||(step==1))
		{
			printf(" Time step %4d\n", step);
		}
		adi();
	}
	timer_stop(1);
	tmax=timer_read(1);
	verify(niter,  & Class,  & verified);
	n3=(((1.0*grid_points[0])*grid_points[1])*grid_points[2]);
	navg=(((grid_points[0]+grid_points[1])+grid_points[2])/3.0);
	if (tmax!=0.0)
	{
		mflops=(((1.0E-6*((double)niter))*(((3478.8*n3)-(17655.7*(navg*navg)))+(28023.7*navg)))/tmax);
	}
	else
	{
		mflops=0.0;
	}
	print_results("BT", Class, grid_points[0], grid_points[1], grid_points[2], niter, tmax, mflops, "          floating point", verified, "3.3.1", "05 May 2022", "gcc", "$(CC)", "-lm", "-I../common", "-g -Wall -O3 -fopenmp -mcmodel=large", "-O3 -fopenmp -mcmodel=large", "(none)");
	if (timeron)
	{
		#pragma cetus private(i) 
		#pragma loop name main#3 
		for (i=1; i<=11; i ++ )
		{
			trecs[i]=timer_read(i);
		}
		if (tmax==0.0)
		{
			tmax=1.0;
		}
		printf("  SECTION   Time (secs)\n");
		#pragma cetus private(i, t) 
		#pragma loop name main#4 
		for (i=1; i<=11; i ++ )
		{
			printf("  %-8s:%9.3f  (%6.2f%%)\n", t_names[i], trecs[i], (trecs[i]*100.0)/tmax);
			if (i==5)
			{
				t=((trecs[2]+trecs[3])+trecs[4]);
				printf("    --> %8s:%9.3f  (%6.2f%%)\n", "sub-rhs", t, (t*100.0)/tmax);
				t=(trecs[5]-t);
				printf("    --> %8s:%9.3f  (%6.2f%%)\n", "rest-rhs", t, (t*100.0)/tmax);
			}
			else
			{
				if (i==8)
				{
					t=((trecs[8]-trecs[9])-trecs[10]);
					printf("    --> %8s:%9.3f  (%6.2f%%)\n", "sub-zsol", t, (t*100.0)/tmax);
				}
				else
				{
					if (i==10)
					{
						t=(trecs[9]+trecs[10]);
						printf("    --> %8s:%9.3f  (%6.2f%%)\n", "redist", t, (t*100.0)/tmax);
					}
				}
			}
		}
	}
	_ret_val_0=0;
	return _ret_val_0;
}
