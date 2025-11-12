//inline” profesional.
package uts1;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

public class kalkulatorsederhana extends JFrame implements ActionListener {
    private final JTextField display = new JTextField("0");
    private double first = 0.0;
    private String op = null;
    private boolean startNew = true;

    public kalkulatorsederhana() {
        super("Kalkulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(display.getFont().deriveFont(24f));
        display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel keys = new JPanel(new GridLayout(6, 4, 8, 8));
        String[] labels = {
            "C", "⌫", "±", "√",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "%", "+",
            "x²", "x³", "mod", "="
        };
        for (String lb : labels) {
            JButton b = new JButton(lb);
            b.setFont(b.getFont().deriveFont(18f));
            b.addActionListener(this);
            keys.add(b);
        }

        setLayout(new BorderLayout(10,10));
        add(display, BorderLayout.NORTH);
        add(keys, BorderLayout.CENTER);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private String fmt(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        DecimalFormat df = new DecimalFormat("0.############");
        return df.format(v);
    }

    private double getVal() {
        try {
            String s = display.getText()
                .replace("×", "*").replace("÷", "/").replace(",", ".");
            // ambil angka terakhir saja
            String[] parts = s.split("[+\\-*/]|mod");
            return Double.parseDouble(parts[parts.length - 1]);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calc(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return b == 0 ? Double.NaN : a / b;
            case "mod": return b == 0 ? Double.NaN : a % b;
        }
        return b;
    }

    private void applyOperator(String newOp, String symbol) {
        double current = getVal();

        // Jika sudah ada operasi sebelumnya dan user lanjut tekan operator lain
        if (op != null && !startNew) {
            double result = calc(first, current, op);
            first = result;
            display.setText(fmt(result) + symbol);
        } else {
            first = current;
            display.setText(fmt(first) + symbol);
        }

        op = newOp;
        startNew = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String t = ((JButton)e.getSource()).getText();

        // angka dan titik
        if ("0123456789".contains(t) || ".".equals(t)) {
            if (startNew) {
                display.setText((".".equals(t)) ? "0." : t);
                startNew = false;
            } else {
                display.setText(display.getText() + t);
            }
            return;
        }

        switch (t) {
            case "C":
                display.setText("0");
                first = 0; op = null; startNew = true;
                break;

            case "⌫":
                String s = display.getText();
                if (s.length() > 1) display.setText(s.substring(0, s.length()-1));
                else display.setText("0");
                break;

            case "±":
                if (!display.getText().equals("0")) {
                    if (display.getText().startsWith("-"))
                        display.setText(display.getText().substring(1));
                    else display.setText("-" + display.getText());
                }
                break;

            // operasi biner (tampilkan simbol dan hitung otomatis jika perlu)
            case "+": applyOperator("+", "+"); break;
            case "-": applyOperator("-", "-"); break;
            case "*": applyOperator("(simbolbintang)", "×"); break;
            case "/": applyOperator("/", "÷"); break;
            case "mod": applyOperator("mod", " mod "); break;

            // operasi unary
            case "%":
                display.setText(fmt(getVal() / 100));
                startNew = true;
                break;
            case "x²":
                display.setText(fmt(Math.pow(getVal(), 2)));
                startNew = true;
                break;
            case "x³":
                display.setText(fmt(Math.pow(getVal(), 3)));
                startNew = true;
                break;
            case "√":
                display.setText(fmt(Math.sqrt(getVal())));
                startNew = true;
                break;

            case "=":
                if (op != null) {
                    double second = getVal();
                    double result = calc(first, second, op);
                    display.setText(fmt(result));
                    op = null;
                    startNew = true;
                }
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new kalkulatorsederhana().setVisible(true));
    }
}