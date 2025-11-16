import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjetoSistemaDeInformacoesClimaticasEmTempoReal {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite o nome da cidade: ");
		String cidade = scanner.nextLine(); // Lê a cidade do teclado
		System.out.println("Nome da cidade digitado: " + cidade);
		try {
			String dadosClimaticos = getDadosClimaticos(cidade); // Retorna um JSON

			// Código 1006 significa localização não é encontrada
			if (dadosClimaticos.contains("\"code\":1006")) { // \"code\":1006 representa "code":1006
				System.out.println("Localização não encontrada. Por favor, tente novamente.");
			} else {
				imprimirDadosClimaticos(dadosClimaticos);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String getDadosClimaticos(String cidade) throws Exception {
		String apiKey = Files.readString( Paths.get("api-key.txt")).trim();

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formataNomeCidade;
		System.out.println("URL: " + apiUrl);
		HttpRequest request = HttpRequest.newBuilder() // Começa a construção de uma solicitação HTTP
				  .uri(URI.create(apiUrl)) // Este método define o URI da solicitação http
				  .build(); // Finaliza a construção da solicitação http

		// Cria objeto enviar solicitações HTTP e receber respostas HTTP, para acessar o site da WeatherAPI
		HttpClient client = HttpClient.newHttpClient();

		// Agora vamos enviar requisições HTTP e receber respostas HTTP, comunicar com o site da API Meteorologica.
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body(); // Retorna os dados meteorológicos obtidos no site da API (WeatherAPR)
	}

	// Método para imprimir os dados meteorológicos de forma organizada
	public static void imprimirDadosClimaticos(String dados) {
		// System.out.println("Dados originais (JSON) obtidos no site meteorológico: " + dados);

		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

		// Extrai os dados da localização
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		// Extrai os dados adicionais
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");
		int umidade = informacoesMeteorologicas.getInt("humidity");
		float velocidadeVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		// Extrai a data e hora da string retornada pela API
		String dataHoraString = informacoesMeteorologicas.getString("last_updated");

		// Imprime as informações atuais
		System.out.println("Informações Meteorológicas para " + cidade + ", " + pais);
		System.out.println("Data e Hora: " + dataHoraString);
		System.out.println("Temperatura Atual: " + temperaturaAtual + "°C");
		System.out.println("Sensação Termica: " + sensacaoTermica + "°C");
		System.out.println("Condição do Tempo: " + condicaoTempo);
		System.out.println("Umidade: " + umidade + "%");
		System.out.println("Velocidade do Vento: " + velocidadeVento + " Km/h");
		System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + " mb");
	}
}