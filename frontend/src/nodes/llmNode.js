import { BaseNode } from "./baseNode";

export const LLMNode = ({ id, data }) => {
  const config = {
    title: "LLM",
    fields: [
      {
        label: "",
        render: () => <span>This is a LLM.</span>,
      },
    ],
    handles: [
      { type: "target", id: "system", style: { top: `${100 / 3}%` } },
      { type: "target", id: "prompt", style: { top: `${200 / 3}%` } },
      { type: "source", id: "response" },
    ],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
