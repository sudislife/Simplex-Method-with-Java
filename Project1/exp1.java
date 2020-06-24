import java.util.Scanner;
import java.lang.*;

class exp1 {
   public static void main(String args[]) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Type in total number of variables: ");
      int nov = sc.nextInt();                                                   // Number of variables

      System.out.print("Type in total number of constraints: ");
      int noc = sc.nextInt();                                                   // Nummber of constraints

      float foc[][] = new float[noc][nov + (2 * noc) + 1];
      float zj[][] = new float[nov + (2 * noc) + 1][2];                         // Zj = Σ Cb * Xib and last column is cj values
      float cb[][] = new float[noc][2];                                         // Cb = Cost of Basic Variable
      int xb[] = new int[noc];
      int igncol[] = new int[2 * noc];
      for(int i = 0; i < igncol.length; i++) igncol[i] = -1;
      for(int i = 0; i < noc; i++)
         cb[i][0] = 0;

      while(true) {
         System.out.print("\nMax Z(Press 1) or Min Z(Press 2): ");
         int z = sc.nextInt();
         System.out.println("Type in objective function equation: ");
         float cj[][] = new float[nov + (2 * noc)][2];                          // Cj = Value of Basic Variables
         float arr2[] = constinput(nov);
         for(int i = 0; i < nov; i++)
            cj[i][0] = arr2[i];

         float delij[][] = new float[nov + (2 * noc)][2];                       // Δij = Cj - Zj

         if(z == 1 || z == 2) {                                                 // Maximize Objective Function
            for(int i = 0; i < noc; i++) {                                      // This loop inputs the constraints
               System.out.println("\nType in the coefficients for equation " + (i + 1) + ":");
               float arr[] = constinput(nov);
               for(int j = 0; j < nov; j++)
                  foc[i][j] = arr[j];
               while(true) {
                  System.out.print("Is the sign less than equal to(Type 1) or more than equal to(type 2) or equal to(type 3): ");
                  int sign = sc.nextInt();
                  if(sign == 2) {
                     foc[i][nov + i] = -1;                                      // Surplus variable
                     foc[i][nov + noc + i] = 1;                                 // Artificial variable
                     xb[i] = nov + noc + i;
                     if(z == 1)
                        cj[nov + noc + i][1] = cb[i][1] = -1;
                     else if(z == 2)
                        cj[nov + noc + i][1] = cb[i][1] = 1;
                     break;
                  }

                  else if(sign == 3) {
                     for(int j = 0; j < igncol.length; j++) {
                        if(igncol[j] == -1) {
                           igncol[j] = nov + i;                                 // Puts surplus variable column in igncol
                           break;
                        }
                     }
                     foc[i][nov + noc + i] = 1;                                 // Artificial variable
                     xb[i] = nov + noc + i;
                     if(z == 1)
                        cj[nov + noc + i][1] = cb[i][1] = -1;
                     else if(z == 2)
                        cj[nov + noc + i][1] = cb[i][1] = 1;
                     break;
                  }

                  else if(sign == 1) {
                     foc[i][nov + i] = 1;                                       // Slack variable
                     xb[i] = nov + i;
                     for(int j = 0; j < igncol.length; j++) {
                        if(igncol[j] == -1) {
                           igncol[j] = nov + noc + i;                           // Puts Artificial variable column in the igncol
                           break;
                        }
                        else
                           continue;
                     }
                     cb[i][1] = 0;
                     break;
                  }

                  else {
                     System.out.println("Input was not '1', '2' or '3', please try again.");
                  }
               }

               System.out.print("Type in value after sign: ");
               foc[i][nov + (2 * noc)] = sc.nextFloat();
            }
            System.out.println("\n\nMatrix 0: ");
            disparrm(foc, noc, nov + (2 * noc) + 1);


            for(int loop = 0; loop < 10; loop++) {
               boolean condmet = true;                                          // Condition met for breaking out of the loop

               System.out.print("\nXb " + loop + ": \n");
               disparr(xb, noc);

               for(int j = 0; j < nov + (2 * noc) + 1; j++) {
                  boolean skip = false;
                  for(int x : igncol) {
                     if(x == j) {
                        skip = true;
                        break;
                     }
                  }
                  if(skip) continue;

                  zj[j][0] = zj[j][1] = 0;

                  for(int i = 0; i < noc; i++) {

                     zj[j][0] += cb[i][0] * foc[i][j];                          // Zj = Σ Cb * Xib
                     zj[j][1] += cb[i][1] * foc[i][j];
                  }
               }

               System.out.println("\nCj " + loop + ":");
               disparr(cj, nov + (2 * noc), 2);

               System.out.println("\nZj " + loop + ": ");
               disparr(zj, nov + (2 * noc) + 1, 2);


               for(int i = 0; i < nov + (2 * noc); i++) {
                  boolean skip = false;
                  for(int x : igncol) {
                     if(x == i) {
                        skip = true;
                        break;
                     }
                  }
                  if(skip) continue;

                  delij[i][0] = cj[i][0] - zj[i][0];                            // Δij = Cj - Zj
                  delij[i][1] = cj[i][1] - zj[i][1];
               }

               for(int i = 0; i < nov + (2 * noc); i++) {
                  boolean skip = false;
                  for(int x : igncol) {
                     if(x == i) {
                        skip = true;
                        break;
                     }
                  }
                  if(skip) continue;

                  if(z == 1) {
                     if(delij[i][1] > 0) {
                        condmet = false;
                        break;
                     }
                     if(delij[i][0] > 0 && delij[i][1] == 0) {
                        condmet = false;
                        break;
                     }
                  }

                  else if(z == 2) {
                     if(delij[i][1] < 0) {
                        condmet = false;
                        break;
                     }
                     if(delij[i][0] < 0 && delij[i][1] == 0) {
                        condmet = false;
                        break;
                     }
                  }
               }

               System.out.println("\ndel ij " + loop + ": ");
               disparr(delij, nov + (2 * noc), 2);

               if(condmet == true) break;                                       // All Δij <= 0 if max and opp for min

               int pivot_col = 0;
               float max_delij = delij[0][1];
               float min_delij = delij[0][1];
               boolean adv1 = true;                                             // All artificial variable del values = 0
               for(int i = 1; i < nov + (2 * noc); i++) {
                  if(z == 1) {
                     boolean skip = false;
                     for(int x : igncol) {
                        if(x == i) {
                           skip = true;
                           break;
                        }
                     }
                     if(skip) continue;

                     if(delij[i][1] > max_delij) {
                        max_delij = delij[i][1];
                        pivot_col = i;
                     }
                     if(delij[i][1] != 0) adv1 = false;
                  }

                  if(z == 2) {
                     boolean skip = false;
                     for(int x : igncol) {
                        if(x == i) {
                           skip = true;
                           break;
                        }
                     }
                     if(skip) continue;

                     if(delij[i][1] < min_delij) {
                        min_delij = delij[i][1];
                        pivot_col = i;
                     }
                     if(delij[i][1] != 0) adv1 = false;
                  }
               }
               if(adv1 == true) {
                  if(z == 1) {
                     max_delij = delij[0][0];                                   // Pivot Column
                     for(int i = 1; i < nov + (2 * noc); i++) {
                        boolean skip = false;
                        for(int x : igncol) {
                           if(x == i) {
                              skip = true;
                              break;
                           }
                        }
                        if(skip) continue;

                        if(delij[i][0] > max_delij) {
                           max_delij = delij[i][0];
                           pivot_col = i;
                        }
                     }
                  }

                  if(z == 2) {
                     min_delij = delij[0][0];                                   // Pivot Column
                     for(int i = 1; i < nov + (2 * noc); i++) {
                        boolean skip = false;
                        for(int x : igncol) {
                           if(x == i) {
                              skip = true;
                              break;
                           }
                        }
                        if(skip) continue;

                        if(delij[i][0] < min_delij) {
                           min_delij = delij[i][0];
                           pivot_col = i;
                        }
                     }
                  }
               }
               System.out.println("\nPivot Col " + loop + " = " + (pivot_col + 1));

               int pivot_row = 0;                                               // Pivot Row
               float minposrt[] = new float[noc];                               // Min positive ratio
               for(int i = 0; i < noc; i++)
                  minposrt[i] = foc[i][nov + (2 * noc)] / foc[i][pivot_col];

               boolean setmin = false;
               float min = 0;

               for(int i = 0; i < noc; i++) {
                  if(minposrt[i] >= 0) {
                     if(foc[i][nov + (2 * noc)] == 0.0) {                       // Skips if 0/-ve
                        if(foc[i][pivot_col] < 0)
                           continue;
                     }

                     if(setmin == false) {
                        min = minposrt[i];
                        pivot_row = i;
                        setmin = true;
                     }


                     else {
                        if(min > minposrt[i]) {
                           min = minposrt[i];
                           pivot_row = i;
                        }

                        else if(min == minposrt[i]) {                           // Degeneracy Condition
                           for(int j = 0; j < noc; j++) {
                              if(foc[pivot_row][nov + j]/foc[pivot_row][pivot_col] > foc[i][nov + j]/foc[i][pivot_col]) {
                                 min = minposrt[i];
                                 pivot_row = i;
                                 break;
                              }

                              else if(foc[pivot_row][nov + j]/foc[pivot_row][pivot_col] < foc[i][nov + j]/foc[i][pivot_col])
                                 break;

                              else
                                 continue;
                           }
                        }

                        else
                           continue;
                     }
                  }
               }

               System.out.println("Pivot Row " + loop + " = " + (pivot_row + 1));

               if(xb[pivot_row] >= nov + noc) {                                 // Adds AV to ignore column array
                  for(int i = 0; i < igncol.length; i++) {
                     if(igncol[i] == -1) {
                        igncol[i] = xb[pivot_row];
                        break;
                     }
                  }
               }

               System.out.print("\nIgncol" + loop + " : \n");
               disparr(igncol, igncol.length);                                  // Displays igncol

               xb[pivot_row] = pivot_col;                                       // Changes Xb to that variable
               cb[pivot_row][0] = cj[pivot_col][0];                             // Changes Cb after init pivot row and pivot col
               cb[pivot_row][1] = cj[pivot_col][1];


               float new_foc[][] = new float[noc][nov + (2 * noc) + 1];
               for(int i = 0; i < noc; i++) {
                  for(int j = 0; j < nov + (2 * noc) + 1; j++) {
                     boolean skip = false;
                     for(int x : igncol) {
                        if(x == j) {
                           skip = true;
                           break;
                        }
                     }
                     if(skip) continue;

                     if(i == pivot_row) new_foc[pivot_row][j] = foc[pivot_row][j] / foc[pivot_row][pivot_col];
                     else if(j == pivot_col) new_foc[i][pivot_col] = 0;
                     else new_foc[i][j] = foc[i][j] - (foc[i][pivot_col] * foc[pivot_row][j] / foc[pivot_row][pivot_col]);
                  }
               }

               System.out.println("\n\n\nMatrix " + (loop + 1) + " :");
               disparrm(new_foc, noc, nov + (2 * noc) + 1);

               for(int i = 0; i < noc; i++)
                  for(int j = 0; j < nov + (2 * noc) + 1; j++)
                     foc[i][j] = new_foc[i][j];
            }

            break;
         }

         else
            System.out.print("Input was not 1 or 2, try again.\n");
      }

      System.out.println("\n\nAns:");
      for(int i = 0; i < noc; i++)
         if(xb[i] < nov)
            System.out.println("x" + (xb[i] + 1) + " = " + foc[i][nov + (2 * noc)]);
         else if(xb[i] >= nov && xb[i] < (nov + noc))
            System.out.println("s" + (xb[i] - nov + 1) + " = " + foc[i][nov + (2 * noc)]);
         else
            System.out.println("A" + (xb[i] - nov - noc + 1) + " = " + foc[i][nov + (2 * noc)]);
      System.out.println("Optimal Solution: " + zj[nov + (2 * noc)][0]);

      System.out.println("\nExecution finished");
   }

   static float[] constinput(int nov) {                                         // To take in the constants for
      float arr[] = new float[nov];                                             // variables
      Scanner sc = new Scanner(System.in);
      for(int i = 0; i < nov; i++) {
         System.out.print("Type in the constant part of x" + (i + 1) + ": ");
         arr[i] = sc.nextFloat();
      }
      return arr;
   }

   static float[] constinput(int nov, int arrsize) {
      float arr[] = new float[arrsize];
      float arr2[] = constinput(nov);
      for(int i = 0; i < arr2.length; i++) arr[i] = arr2[i];
      return arr;
   }

   static void disparr(float arr[][], int m, int n) {                           // Display 2d array
      for(int j = 0; j < n; j++) {
         for(int i = 0; i < m; i++) {
            if(i == 0)
               System.out.print("[ " + arr[i][j] + ", ");
            else if(i == (m - 1))
               System.out.println(arr[i][j] + " ]");
            else
               System.out.print(arr[i][j] + ", ");
         }
      }
   }

   static void disparrm(float arr[][], int m, int n) {                          // Display 2d array
      for(int i = 0; i < m; i++) {
         for(int j = 0; j < n; j++) {
            if(j == 0)
               System.out.print("[ " + arr[i][j] + ", ");
            else if(j == (n - 1))
               System.out.println(arr[i][j] + " ]");
            else
               System.out.print(arr[i][j] + ", ");
         }
      }
   }

   static void disparr(float arr[], int n) {
      for(int j = 0; j < n; j++) {
         if(j == 0)
            System.out.print("[ " + arr[j] + ", ");
         else if(j == (n - 1))
            System.out.println(arr[j] + " ]");
         else
            System.out.print(arr[j] + ", ");
      }
   }

   static void disparr(int arr[], int n) {
      for(int j = 0; j < n; j++) {
         if(j == 0) {
            System.out.print("[ " + arr[j] + ", ");
            continue;
         }
         if(j == (n - 1))
            System.out.println(arr[j] + " ]");
         else
            System.out.print(arr[j] + ", ");
      }
   }
}
