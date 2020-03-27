import java.util.Scanner;
import java.lang.*;

class exp1 {
   public static void main(String args[]) {
      Scanner sc = new Scanner(System.in);
      System.out.print("Type in total number of variables: ");
      int nov = sc.nextInt();                                                   // Number of variables

      System.out.print("Type in total number of constraints: ");
      int noc = sc.nextInt();                                                   // Nummber of constraints

      float foc[][] = new float[noc][nov + noc + 1];
      float zj[] = new float[nov + noc + 1];                                    // Zj = Σ Cb * Xib
      float cb[] = new float[noc];
      float xb[] = new float[noc];
      for(int i = 0; i < noc; i++) {
         cb[i] = 0;
         xb[i] = nov + i;
      }

      while(true) {
         System.out.print("\nMax Z(Press 1) or Min Z(Press 2): ");
         int z = sc.nextInt();
         System.out.println("Type in objective function equation: ");
         float cj[] = new float[nov + noc];                                     // Cj = Cost of Basic Variables
         cj = constinput(nov, nov + noc);

         System.out.println("cj : ");
         disparr(cj, nov + noc);

         float delij[] = new float[nov + noc];                                  // Δij = Cj - Zj

         if(z == 1) {                                                           // Maximize Objective Function
            for(int i = 0; i < noc; i++) {                                      // This loop inputs the constraints
               System.out.println("\nType in function of constraints for equation " + (i + 1) + ":");
               float arr[] = constinput(nov);
               for(int j = 0; j < nov; j++)
                  foc[i][j] = arr[j];
               foc[i][nov + i] = 1;
               System.out.print("Type in value after sign: ");
               foc[i][nov + noc] = sc.nextFloat();

               while(true) {
                  System.out.print("Is the sign less than equal to(Type 1) or more than equal to(type 2): ");
                  int sign = sc.nextInt();
                  if(sign == 2) {
                     for(int j = 0; j < nov; j++)
                        foc[i][j] = -foc[i][j];
                     foc[i][nov + noc] = -foc[i][nov + noc];                    // bj cj define
                     break;
                  }

                  else if(sign != 1)
                     System.out.println("Input was not '1' or '2', please try again.");

                  else
                     break;
               }
            }

            System.out.println("Matrix: ");
            disparr(foc, noc, nov + noc + 1);

            while(true) {
               boolean condmet = true;
               for(int j = 0; j < nov + noc + 1; j++)
                  for(int i = 0; i < noc; i++)
                     zj[j] += cb[i] * foc[i][j];                                // Zj = Σ Cb * Xib

               System.out.println("Cj : ");
               disparr(cj, nov + noc);

               System.out.println("Zj : ");
               disparr(zj, nov + noc + 1);


               for(int i = 0; i < nov + noc; i++)
                  delij[i] = cj[i] - zj[i];                                     // Δij = Cj - Zj

               for(int i = 0; i < nov + noc; i++) {
                  if(delij[i] > 0) {
                     condmet = false;
                     break;
                  }
               }

               System.out.println("Δij : ");
               disparr(delij, nov + noc);

               if(condmet == true) break;                                       // All Δij <= 0

               int pivot_col = 0;
               float max_delij = delij[0];                                      // Pivot Column
               for(int i = 1; i < noc + nov; i++) {
                  if(delij[i] > max_delij) {
                     max_delij = delij[i];
                     pivot_col = i;
                  }
               }
               System.out.println("\nPivot Col = " + pivot_col);

               int pivot_row = -1;                                              // Pivot Row
               float minposrt[] = new float[noc];                               // Min positive ratio
               for(int i = 0; i < noc; i++)
                  minposrt[i] = foc[i][noc + nov] / foc[i][pivot_col];

               boolean setmin = false;
               float min = 0;

               for(int i = 0; i < noc; i++) {
                  if(minposrt[i] >= 0) {
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

               System.out.println("\nPivot Row = " + pivot_row);

               xb[pivot_row] = pivot_col;                                       // Changes Xb to that variable
               cb[pivot_row] = cj[pivot_col];                                   // Changes Cb after init pivot row and pivot col

               float new_foc[][] = new float[noc][nov + noc + 1];
               for(int i = 0; i < noc; i++) {
                  for(int j = 0; j < nov + noc + 1; j++) {
                     if(i == pivot_row) new_foc[pivot_row][j] = foc[pivot_row][j] / foc[pivot_row][pivot_col];
                     else if(j == pivot_col) new_foc[i][pivot_col] = 0;
                     else new_foc[i][j] = foc[i][j] - (foc[i][pivot_col] * foc[pivot_row][j] / foc[pivot_row][pivot_col]);
                  }
               }

               System.out.println("\nMatrix:");
               disparr(new_foc, noc, nov + noc + 1);

               for(int i = 0; i < noc; i++)
                  for(int j = 0; j < nov + noc + 1; j++)
                     foc[i][j] = new_foc[i][j];
            }

            break;
         }

         else if(z == 2) {                                                      // Minimize Objective Function
            for(int i = 0; i < noc; i++) {                                      // This loop inputs the constraints
               System.out.println("\nType in function of constraints for equation " + (i + 1) + ":");
               float arr[] = constinput(nov);
               for(int j = 0; j < nov; j++)
                  foc[i][j] = arr[j];
               foc[i][nov + i] = 1;
               System.out.print("Type in value after sign: ");
               foc[i][nov + noc] = sc.nextFloat();

               while(true) {
                  System.out.print("Is the sign less than equal to(Type 1) or more than equal to(type 2): ");
                  int sign = sc.nextInt();
                  if(sign == 1) {
                     for(int j = 0; j < nov; j++)
                        foc[i][j] = -foc[i][j];
                     foc[i][nov + noc] = -foc[i][nov + noc];                    // bj cj define
                     break;
                  }

                  else if(sign != 2)
                     System.out.println("Input was not '1' or '2', please try again.");

                  else
                     break;
               }
            }

            System.out.println("Matrix: ");
            disparr(foc, noc, nov + noc + 1);

            while(true) {
               boolean condmet = true;
               for(int j = 0; j < nov + noc + 1; j++)
                  for(int i = 0; i < noc; i++)
                     zj[j] += cb[i] * foc[i][j];                                // Zj = Σ Cb * Xib

               System.out.println("Cj : ");
               disparr(cj, nov + noc);

               System.out.println("Zj : ");
               disparr(zj, nov + noc + 1);


               for(int i = 0; i < nov + noc; i++)
                  delij[i] = cj[i] - zj[i];                                     // Δij = Cj - Zj

               for(int i = 0; i < nov + noc; i++) {
                  if(delij[i] > 0) {
                     condmet = false;
                     break;
                  }
               }

               System.out.println("Δij : ");
               disparr(delij, nov + noc);

               if(condmet == true) break;                                       // All Δij <= 0

               int pivot_col = 0;
               float max_delij = delij[0];                                      // Pivot Column
               for(int i = 1; i < noc + nov; i++) {
                  if(delij[i] > max_delij) {
                     max_delij = delij[i];
                     pivot_col = i;
                  }
               }
               System.out.println("\nPivot Col = " + pivot_col);

               int pivot_row = -1;                                              // Pivot Row
               float minposrt[] = new float[noc];                               // Min positive ratio
               for(int i = 0; i < noc; i++)
                  minposrt[i] = foc[i][noc + nov] / foc[i][pivot_col];

               boolean setmin = false;
               float min = 0;

               for(int i = 0; i < noc; i++) {
                  if(minposrt[i] >= 0) {
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

               System.out.println("\nPivot Row = " + pivot_row);

               xb[pivot_row] = pivot_col;                                       // Changes Xb to that variable
               cb[pivot_row] = cj[pivot_col];                                   // Changes Cb after init pivot row and pivot col

               float new_foc[][] = new float[noc][nov + noc + 1];
               for(int i = 0; i < noc; i++) {
                  for(int j = 0; j < nov + noc + 1; j++) {
                     if(i == pivot_row) new_foc[pivot_row][j] = foc[pivot_row][j] / foc[pivot_row][pivot_col];
                     else if(j == pivot_col) new_foc[i][pivot_col] = 0;
                     else new_foc[i][j] = foc[i][j] - (foc[i][pivot_col] * foc[pivot_row][j] / foc[pivot_row][pivot_col]);
                  }
               }

               System.out.println("\nMatrix:");
               disparr(new_foc, noc, nov + noc + 1);

               for(int i = 0; i < noc; i++)
                  for(int j = 0; j < nov + noc + 1; j++)
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
            System.out.println("x" + (xb[i] + 1) + " = " + foc[i][nov + noc]);
         else
            System.out.println("s" + (xb[i] - nov + 1) + " = " + foc[i][nov + noc]);
      System.out.println("Optimal Solution: " + zj[nov + noc]);

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
}
