  import { BaseNode } from "./baseNode";

  export const ApiNode = ({ id, data }) => {
    const config = {
      title: "API Call",
      fields: [],
      handles: [
        { type: "target", id: "url" },
        { type: "source", id: "response" },
      ],
    };

    return <BaseNode id={id} data={data} config={config} />;
  };