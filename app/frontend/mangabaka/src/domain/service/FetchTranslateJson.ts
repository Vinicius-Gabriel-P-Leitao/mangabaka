import type { ApiResponse } from "@/application/type/ApiResponse";

export async function FetchTranslateJson<T>(
  url: string,
): Promise<ApiResponse<T>> {
  const response: Response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Erro ao carregar tradução ${response.status}`);
  }

  const json: T = await response.json();

  return {
    data: json,
    status: response.status,
    statusText: response.statusText,
  };
}
