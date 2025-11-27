public class DeliveryCalculator {

    public double calculateDeliveryCost(int distance, String dimensions, boolean isFragile, String workload) {
        if (isFragile && distance > 30) {
            throw new IllegalArgumentException("Хрупкие грузы нельзя возить на расстояние более 30 км");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("Расстояние должно быть положительным числом");
        }

        double cost = 0;

        if (distance > 30) {
            cost += 300;
        } else if (distance > 10) {
            cost += 200;
        } else if (distance > 2) {
            cost += 100;
        } else {
            cost += 50;
        }

        if ("large".equals(dimensions)) {
            cost += 200;
        } else if ("small".equals(dimensions)) {
            cost += 100;
        } else {
            throw new IllegalArgumentException("неверные габариты груза. Используйте 'small' или 'large'");
        }

        if (isFragile) {
            cost += 300;
        }

        double coefficient = getWorkloadCoefficient(workload);
        cost *= coefficient;

        return Math.max(cost, 400);
    }

    private double getWorkloadCoefficient(String workload) {
        return switch (workload) {
            case "very_high" -> 1.6;
            case "high" -> 1.4;
            case "increased" -> 1.2;
            case "normal" -> 1.0;
            default -> throw new IllegalArgumentException("Неверный уровень загруженности. Используйте: normal, increased, high, very_high");
        };
    }
}
