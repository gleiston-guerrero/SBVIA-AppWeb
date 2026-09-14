import csv
import sys

def calculate_sus():
    try:
        with open('sus-raw-data.csv', 'r') as f:
            reader = csv.DictReader(f)
            
            total_sum = 0
            valid_responses = 0
            
            print("=========================================")
            print("      CÁLCULO DE SUS - RESULTADOS        ")
            print("=========================================")
            
            for row in reader:
                participant = row['participant_id']
                try:
                    q1 = int(row['Q1'])
                    q2 = int(row['Q2'])
                    q3 = int(row['Q3'])
                    q4 = int(row['Q4'])
                    q5 = int(row['Q5'])
                    q6 = int(row['Q6'])
                    q7 = int(row['Q7'])
                    q8 = int(row['Q8'])
                    q9 = int(row['Q9'])
                    q10 = int(row['Q10'])
                    expected_score = float(row['sus_score'])
                except ValueError:
                    print(f"Error: Datos inválidos para el participante {participant}")
                    continue
                
                # Fórmula SUS
                # Impares: respuesta - 1
                contrib_impares = (q1 - 1) + (q3 - 1) + (q5 - 1) + (q7 - 1) + (q9 - 1)
                # Pares: 5 - respuesta
                contrib_pares = (5 - q2) + (5 - q4) + (5 - q6) + (5 - q8) + (5 - q10)
                
                sus_individual = (contrib_impares + contrib_pares) * 2.5
                
                if abs(sus_individual - expected_score) > 0.01:
                    print(f"[{participant}] DISCREPANCIA: CSV dice {expected_score}, Fórmula dice {sus_individual}")
                else:
                    print(f"[{participant}] OK -> Impares: {contrib_impares}, Pares: {contrib_pares} | Total = {sus_individual}")
                
                total_sum += sus_individual
                valid_responses += 1
                
            print("=========================================")
            print(f"Participantes válidos: {valid_responses}")
            print(f"Suma total de scores:  {total_sum}")
            if valid_responses > 0:
                promedio = total_sum / valid_responses
                print(f"Promedio SUS:          {promedio:.2f}")
            print("=========================================")

    except FileNotFoundError:
        print("Error: No se encontró el archivo sus-raw-data.csv")
        sys.exit(1)

if __name__ == '__main__':
    calculate_sus()
